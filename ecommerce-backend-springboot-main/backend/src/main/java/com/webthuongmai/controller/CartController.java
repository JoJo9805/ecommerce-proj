package com.webthuongmai.controller;
import com.webthuongmai.repository.CartItemRepository;
import com.webthuongmai.repository.CartRepository;
import com.webthuongmai.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.webthuongmai.entity.*;
import java.util.Optional;
import com.webthuongmai.dto.CartDTO;
import com.webthuongmai.entity.CartItem;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin("*")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCartByUser(@PathVariable Long userId) {

        List<CartItem> items = cartItemRepository.findCartItemsByUser(userId);

        List<CartDTO> result = items.stream().map(ci -> {
            CartDTO dto = new CartDTO();
            dto.cartItemId = ci.getCartItemID();
            dto.variantId = ci.getProductVariant().getVariantID();
            Product p = ci.getProductVariant().getProduct();
            dto.productName = p.getProductName();
            dto.shopName = p.getShop().getShopName();
            dto.price = ci.getProductVariant().getPrice();
            dto.quantity = ci.getQuantity();

            // Lấy ảnh chính thật của sản phẩm (ưu tiên isMain, nếu không có thì lấy ảnh đầu tiên)
            dto.image = null;
            if (p.getImages() != null && !p.getImages().isEmpty()) {
                dto.image = p.getImages().stream()
                        .filter(img -> Boolean.TRUE.equals(img.getIsMain()))
                        .map(ProductImage::getImageURL)
                        .findFirst()
                        .orElse(p.getImages().get(0).getImageURL());
            }
            return dto;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public Cart create(@RequestBody Cart cart) {
        return cartService.createCart(cart);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody com.webthuongmai.dto.AddToCartRequest request) {

        Long userId = request.getUserId();
        Long variantId = request.getVariantId();
        Integer quantity = request.getQuantity() == null ? 1 : request.getQuantity();

        if (userId == null) {
            return ResponseEntity.badRequest().body("Thiếu userId");
        }
        if (variantId == null) {
            return ResponseEntity.badRequest().body("Sản phẩm này chưa có phân loại để thêm vào giỏ");
        }
        if (quantity < 1) {
            return ResponseEntity.badRequest().body("Số lượng phải lớn hơn hoặc bằng 1");
        }

        Cart cart = cartRepository.findByUser_UserID(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    User user = new User();
                    user.setUserID(userId);
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
        Optional<CartItem> existingItem = cartItemRepository
                .findByCart_CartIDAndProductVariant_VariantID(cart.getCartID(), variantId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
            return ResponseEntity.ok("Đã tăng số lượng sản phẩm trong giỏ");
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);

            ProductVariant variant = new ProductVariant();
            variant.setVariantID(variantId);
            newItem.setProductVariant(variant);

            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
            return ResponseEntity.ok("Đã thêm sản phẩm mới vào giỏ hàng");
        }
    }
}