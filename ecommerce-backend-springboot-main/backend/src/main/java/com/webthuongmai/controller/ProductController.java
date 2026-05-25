package com.webthuongmai.controller;

import com.webthuongmai.dto.SellerProductRequest;
import com.webthuongmai.entity.Product;
import com.webthuongmai.dto.ProductCardDTO;
import com.webthuongmai.dto.ProductDetailDTO;
import com.webthuongmai.repository.ProductRepository;
import com.webthuongmai.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // Lấy danh sách sản phẩm CÓ PHÂN TRANG (kèm ảnh, giá thấp nhất, variantID)
    // Ví dụ: /api/products?page=0&size=40
    @GetMapping
    public Page<ProductCardDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "40") int size) {
        return productService.getProductCardsPaged(page, size);
    }

    // Hiển thị sản phẩm Trending (Cho "Sản phẩm bán chạy" hoặc Acc mới)
    @GetMapping("/trending")
    public ResponseEntity<List<Product>> getTrending() {
        return ResponseEntity.ok(productService.getTrendingProducts(10));
    }

    // Hiển thị theo lịch sử tìm kiếm (Gợi ý cho User)
    @GetMapping("/recommendations")
    public ResponseEntity<List<Product>> getRecommendations(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(productService.getRecommendedProducts(userId));
    }

    // Tìm kiếm và Lọc theo danh mục CÓ PHÂN TRANG (Bên trái menu)
    // Ví dụ: /api/products/search?keyword=ao&page=0&size=40
    @GetMapping("/search")
    public Page<ProductCardDTO> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "40") int size
    ) {
        return productService.searchProductCardsPaged(keyword, categoryId, page, size);
    }

    // Lấy sản phẩm tương tự
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<Product>> getSimilarProducts(
            @PathVariable Long id,
            @RequestParam Long categoryId) {
        return ResponseEntity.ok(productRepository.findByCategory_CategoryIDAndProductIDNot(categoryId, id));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Product>> getOtherFromShop(
            @PathVariable Long shopId,
            @RequestParam(required = false) Long excludeId) {
        return ResponseEntity.ok(productService.getOtherProductsByShop(shopId, excludeId));
    }

    // Tất cả sản phẩm của shop kèm ảnh + giá + variantID (cho trang Shop)
    @GetMapping("/shop/{shopId}/all")
    public ResponseEntity<List<ProductCardDTO>> getAllFromShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(productService.getShopProductCards(shopId));
    }

    @GetMapping("/{id}/related-by-category")
    public ResponseEntity<List<Product>> getRelatedProductsForDetail(
            @PathVariable Long id,
            @RequestParam Long categoryId) {
        return ResponseEntity.ok(productService.getSimilarProducts(categoryId, id));
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    /**
     * [SELLER] API tạo mới sản phẩm KÈM variant và image trong cùng 1 request.
     * POST /api/products/seller
     * Body: SellerProductRequest (productName, brand, description, categoryId, shopId, variants[], images[])
     */
    @PostMapping("/seller")
    public ResponseEntity<?> createWithDetails(@RequestBody SellerProductRequest request) {
        try {
            ProductDetailDTO result = productService.createProductWithDetails(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Trả về message lỗi chi tiết để dễ debug
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi tạo sản phẩm"));
        }
    }

    // Hiển thị thông tin chi tiết sản phẩm (kèm variants + reviews)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductDetail(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Cập nhật chỉnh sửa sản phẩm (chỉ fields cơ bản - giữ để backward compatible)
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, product));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * [SELLER] API cập nhật sản phẩm KÈM variant và image trong cùng 1 request.
     * PUT /api/products/seller/{id}
     * Body: SellerProductRequest (có thể gửi variantID/imageID để update, null để tạo mới)
     */
    @PutMapping("/seller/{id}")
    public ResponseEntity<?> updateWithDetails(
            @PathVariable Long id,
            @RequestBody SellerProductRequest request) {
        try {
            return ResponseEntity.ok(productService.updateProductWithDetails(id, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi cập nhật sản phẩm"));
        }
    }

    /**
     * [SELLER] Xóa sản phẩm kèm toàn bộ variant và image liên quan.
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/collection/{type}")
    public Page<ProductCardDTO> getByCollection(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "40") int size) {
        return productService.getProductsByCollection(type, page, size);
    }
}