package com.webthuongmai.service;

import com.webthuongmai.dto.SellerProductRequest;
import com.webthuongmai.entity.Category;
import com.webthuongmai.entity.Product;
import com.webthuongmai.entity.Shop;
import com.webthuongmai.dto.ProductCardDTO;
import com.webthuongmai.dto.ProductDetailDTO;
import com.webthuongmai.entity.ProductImage;
import com.webthuongmai.entity.ProductVariant;
import com.webthuongmai.entity.Review;
import com.webthuongmai.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ShopRepository shopRepository;

    // Phân rã từ khóa ra làm các từ đơn lẻ để truyền vào tham số Query SQL Server
    // Sửa lại hàm này trong ProductService.java của bạn
    private String[] getKeywords(String keyword) {
        if (keyword == null || keyword.isBlank()) return new String[]{"%%", null};

        // Tách chuỗi thành mảng các từ đơn
        String[] words = keyword.trim().toLowerCase().split("\\s+");

        // Sắp xếp các từ theo độ dài giảm dần (Từ dài nhất lên đầu để làm từ khóa chính)
        java.util.Arrays.sort(words, (a, b) -> Integer.compare(b.length(), a.length()));

        String w1 = "%" + words[0] + "%";
        String w2 = null;

        // Nếu người dùng nhập từ 2 từ trở lên, từ dài thứ nhì sẽ được gán cho w2
        if (words.length > 1) {
            w2 = "%" + words[1] + "%";
        }

        return new String[]{w1, w2};
    }

    // ====== DANH SÁCH SẢN PHẨM (trả về DTO có ảnh + giá + variantID) ======

    // ----- PHÂN TRANG (dùng cho trang chủ khi có hàng nghìn sản phẩm) -----

    public Page<ProductCardDTO> getProductCardsPaged(int page, int size) {
        return searchProductCardsPaged(null, null, page, size);
    }

    public Page<ProductCardDTO> searchProductCardsPaged(String keyword, Long categoryId, int page, int size) {
        if (size <= 0) size = 40;
        if (size > 100) size = 100;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        Page<Product> productPage;

        if (hasKeyword && categoryId != null) {
            String[] tokens = getKeywords(keyword);
            productPage = productRepository.searchProductsByCategoryAdvanced(tokens[0], tokens[1], categoryId, pageable);
        } else if (hasKeyword) {
            String[] tokens = getKeywords(keyword);
            productPage = productRepository.searchProductsAdvanced(tokens[0], tokens[1], pageable);
        } else if (categoryId != null) {
            productPage = productRepository.findByCategory_CategoryID(categoryId, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductCardDTO> cards = toCards(productPage.getContent());
        return new PageImpl<>(cards, pageable, productPage.getTotalElements());
    }

    // ----- KHÔNG phân trang -----

    public List<ProductCardDTO> getAllProductCards() {
        return toCards(productRepository.findAll());
    }

    public List<ProductCardDTO> searchProductCards(String keyword, Long categoryId) {
        List<Product> products;
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasKeyword && categoryId != null) {
            String[] tokens = getKeywords(keyword);
            products = productRepository.searchProductsByCategoryAdvanced(tokens[0], tokens[1], categoryId);
        } else if (hasKeyword) {
            String[] tokens = getKeywords(keyword);
            products = productRepository.searchProductsAdvanced(tokens[0], tokens[1]);
        } else if (categoryId != null) {
            products = productRepository.findByCategory_CategoryID(categoryId);
        } else {
            products = productRepository.findAll();
        }
        return toCards(products);
    }

    private List<ProductCardDTO> toCards(List<Product> products) {
        if (products.isEmpty()) return new java.util.ArrayList<>();

        List<Long> ids = products.stream()
                .map(Product::getProductID)
                .collect(Collectors.toList());

        Map<Long, List<ProductVariant>> variantsByProduct =
                productVariantRepository.findByProduct_ProductIDIn(ids).stream()
                        .filter(v -> v.getProduct() != null)
                        .collect(Collectors.groupingBy(v -> v.getProduct().getProductID()));

        Map<Long, List<ProductImage>> imagesByProduct =
                productImageRepository.findByProduct_ProductIDInOrderByIsMainDesc(ids).stream()
                        .filter(img -> img.getProduct() != null)
                        .collect(Collectors.groupingBy(img -> img.getProduct().getProductID()));

        List<ProductCardDTO> result = new java.util.ArrayList<>();
        for (Product p : products) {
            result.add(toCard(p,
                    variantsByProduct.getOrDefault(p.getProductID(), java.util.Collections.emptyList()),
                    imagesByProduct.getOrDefault(p.getProductID(), java.util.Collections.emptyList())));
        }
        return result;
    }

    private ProductCardDTO toCard(Product p, List<ProductVariant> variants, List<ProductImage> images) {
        ProductCardDTO dto = new ProductCardDTO();
        dto.setProductID(p.getProductID());
        dto.setProductName(p.getProductName());
        dto.setBrand(p.getBrand());

        Double avgRating = reviewRepository.getAverageRatingByProductId(p.getProductID());

        if (avgRating != null && avgRating > 0) {
            dto.setRating((double) Math.round(avgRating * 10) / 10.0);
        } else {
            // Nếu sản phẩm chưa có đánh giá, lấy lại điểm mặc định từ Database hoặc trả về 5.0
            dto.setRating(p.getRating() != null && p.getRating() > 0 ? p.getRating() : 5.0);
        }

        dto.setSoldCount(orderItemRepository.countSoldByProductId(p.getProductID()));

        if (p.getCategory() != null) dto.setCategoryID(p.getCategory().getCategoryID());
        if (p.getShop() != null) {
            dto.setShopID(p.getShop().getShopID());
            dto.setShopRating(p.getShop().getRating());
        }

        variants.stream()
                .filter(v -> v.getPrice() != null)
                .min(Comparator.comparing(ProductVariant::getPrice))
                .ifPresent(v -> {
                    dto.setPrice(v.getPrice());
                    dto.setVariantID(v.getVariantID());
                });

        if (!images.isEmpty()) {
            dto.setImageURL(images.get(0).getImageURL());
        }

        return dto;
    }

    // ====== CÁC HÀM CŨ (giữ nguyên) ======
    public List<Product> getAllProducts() { return productRepository.findAll(); }
    public Product createProduct(Product product) { return productRepository.save(product); }
    public List<Product> getProductsByCategoryId(Long categoryId) { return productRepository.findByCategory_CategoryID(categoryId); }
    public List<Product> getTrendingProducts(int limit) { return productRepository.findTrendingProducts(PageRequest.of(0, limit)); }
    public List<Product> getRecommendedProducts(Long userId) { return getTrendingProducts(10); }
    public List<Product> getOtherProductsByShop(Long shopId, Long productId) {
        if (productId == null) return productRepository.findByShop_ShopID(shopId);
        return productRepository.findByShop_ShopIDAndProductIDNot(shopId, productId);
    }
    public List<ProductCardDTO> getShopProductCards(Long shopId) { return toCards(productRepository.findByShop_ShopID(shopId)); }
    public List<Product> getSimilarProducts(Long categoryId, Long productId) { return productRepository.findByCategory_CategoryIDAndProductIDNot(categoryId, productId); }

    // ====== TẠO MỚI SẢN PHẨM KÈM VARIANT + IMAGE (dành cho Seller) ======
    @Transactional
    public ProductDetailDTO createProductWithDetails(SellerProductRequest request) {
        // 1. Tạo Product
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // 2. Gắn Category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        // 3. Gắn Shop
        if (request.getShopId() != null) {
            Shop shop = shopRepository.findById(request.getShopId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy shop ID: " + request.getShopId()));
            product.setShop(shop);
        }

        Product savedProduct = productRepository.save(product);

        // 4. Lưu Variants
        List<ProductVariant> savedVariants = new ArrayList<>();
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (SellerProductRequest.VariantDTO vDto : request.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(savedProduct);
                variant.setSize(vDto.getSize());
                variant.setColor(vDto.getColor());
                variant.setPrice(vDto.getPrice());
                variant.setStockQuantity(vDto.getStockQuantity() != null ? vDto.getStockQuantity() : 0);
                variant.setSku(vDto.getSku());
                variant.setStatus(vDto.getStatus() != null ? vDto.getStatus() : "active");
                variant.setCreatedAt(LocalDateTime.now());
                variant.setUpdatedAt(LocalDateTime.now());
                savedVariants.add(productVariantRepository.save(variant));
            }
        }

        // 5. Lưu Images
        List<ProductImage> savedImages = new ArrayList<>();
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            boolean hasMain = request.getImages().stream().anyMatch(img -> Boolean.TRUE.equals(img.getIsMain()));
            boolean firstSet = false;
            for (SellerProductRequest.ImageDTO imgDto : request.getImages()) {
                ProductImage image = new ProductImage();
                image.setProduct(savedProduct);
                image.setImageURL(imgDto.getImageURL());
                // Nếu không có ảnh nào được đánh dấu isMain, tự động đặt ảnh đầu tiên làm ảnh chính
                if (!hasMain && !firstSet) {
                    image.setIsMain(true);
                    firstSet = true;
                } else {
                    image.setIsMain(Boolean.TRUE.equals(imgDto.getIsMain()));
                }
                image.setCreatedAt(LocalDateTime.now());
                savedImages.add(productImageRepository.save(image));
            }
        }

        Product fullyLoadedProduct = productRepository.findById(savedProduct.getProductID()).orElse(savedProduct);
        return new ProductDetailDTO(fullyLoadedProduct, savedVariants, savedImages, new ArrayList<>());
    }

    // ====== CẬP NHẬT SẢN PHẨM KÈM VARIANT + IMAGE (dành cho Seller) ======
    @Transactional
    public ProductDetailDTO updateProductWithDetails(Long productId, SellerProductRequest request) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId));

        // 1. Cập nhật thông tin cơ bản
        existing.setProductName(request.getProductName());
        existing.setDescription(request.getDescription());
        existing.setBrand(request.getBrand());
        existing.setUpdatedAt(LocalDateTime.now());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục ID: " + request.getCategoryId()));
            existing.setCategory(category);
        }

        productRepository.save(existing);

        // 2. Cập nhật Variants
        // Lấy danh sách variant hiện có của sản phẩm
        List<ProductVariant> currentVariants = productVariantRepository.findByProduct_ProductID(productId);

        if (request.getVariants() != null) {
            // Xác định IDs mới gửi lên
            List<Long> newVariantIds = request.getVariants().stream()
                    .filter(v -> v.getVariantID() != null)
                    .map(SellerProductRequest.VariantDTO::getVariantID)
                    .collect(Collectors.toList());

            // Xóa các variant không còn trong danh sách mới
            for (ProductVariant cv : currentVariants) {
                if (!newVariantIds.contains(cv.getVariantID())) {
                    productVariantRepository.delete(cv);
                }
            }

            // Thêm mới hoặc cập nhật từng variant
            for (SellerProductRequest.VariantDTO vDto : request.getVariants()) {
                ProductVariant variant;
                if (vDto.getVariantID() != null) {
                    // Cập nhật variant đã tồn tại
                    variant = productVariantRepository.findById(vDto.getVariantID())
                            .orElse(new ProductVariant());
                } else {
                    // Tạo variant mới
                    variant = new ProductVariant();
                    variant.setCreatedAt(LocalDateTime.now());
                }
                variant.setProduct(existing);
                variant.setSize(vDto.getSize());
                variant.setColor(vDto.getColor());
                variant.setPrice(vDto.getPrice());
                variant.setStockQuantity(vDto.getStockQuantity() != null ? vDto.getStockQuantity() : 0);
                variant.setSku(vDto.getSku());
                variant.setStatus(vDto.getStatus() != null ? vDto.getStatus() : "active");
                variant.setUpdatedAt(LocalDateTime.now());
                productVariantRepository.save(variant);
            }
        }

        // 3. Cập nhật Images
        List<ProductImage> currentImages = productImageRepository.findByProduct_ProductIDOrderByIsMainDesc(productId);

        if (request.getImages() != null) {
            // Xác định IDs mới gửi lên
            List<Long> newImageIds = request.getImages().stream()
                    .filter(img -> img.getImageID() != null)
                    .map(SellerProductRequest.ImageDTO::getImageID)
                    .collect(Collectors.toList());

            // Xóa ảnh không còn trong danh sách mới
            for (ProductImage ci : currentImages) {
                if (!newImageIds.contains(ci.getImageID())) {
                    productImageRepository.delete(ci);
                }
            }

            // Thêm mới hoặc cập nhật từng ảnh
            boolean hasMain = request.getImages().stream().anyMatch(img -> Boolean.TRUE.equals(img.getIsMain()));
            boolean firstSet = false;
            for (SellerProductRequest.ImageDTO imgDto : request.getImages()) {
                ProductImage image;
                if (imgDto.getImageID() != null) {
                    image = productImageRepository.findById(imgDto.getImageID())
                            .orElse(new ProductImage());
                } else {
                    image = new ProductImage();
                    image.setCreatedAt(LocalDateTime.now());
                }
                image.setProduct(existing);
                image.setImageURL(imgDto.getImageURL());
                if (!hasMain && !firstSet) {
                    image.setIsMain(true);
                    firstSet = true;
                } else {
                    image.setIsMain(Boolean.TRUE.equals(imgDto.getIsMain()));
                }
                productImageRepository.save(image);
            }
        }

        // Trả về DTO đầy đủ sau khi cập nhật
        List<ProductVariant> updatedVariants = productVariantRepository.findByProduct_ProductID(productId);
        List<ProductImage> updatedImages = productImageRepository.findByProduct_ProductIDOrderByIsMainDesc(productId);
        List<Review> reviews = reviewRepository.findByProduct_ProductID(productId);

        return new ProductDetailDTO(existing, updatedVariants, updatedImages, reviews);
    }

    public ProductDetailDTO getProductDetail(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        List<ProductVariant> variants = productVariantRepository.findByProduct_ProductID(productId);
        List<ProductImage> images = productImageRepository.findByProduct_ProductIDOrderByIsMainDesc(productId);
        List<Review> reviews = reviewRepository.findByProduct_ProductID(productId);

        // TÍNH TOÁN LƯỢT BÁN VÀ ĐIỂM ĐÁNH GIÁ THỰC TẾ
        int sold = orderItemRepository.countSoldByProductId(productId);
        product.setSoldCount(sold);
        if (!reviews.isEmpty()) {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(5.0);
            product.setRating((double) Math.round(avg * 10) / 10);
            productRepository.save(product); // Cập nhật lại sao chuẩn vào DB
        }

        // Gán tổng số sản phẩm và tổng lượt bán của Shop cho thẻ Shop Card
        if (product.getShop() != null) {
            Long shopId = product.getShop().getShopID();
            product.setShopTotalSales(orderItemRepository.countSoldByShopId(shopId));
            product.setShopProductCount(productRepository.countByShop_ShopID(shopId));
        }

        return new ProductDetailDTO(product, variants, images, reviews);
    }

    public Product updateProduct(Long productId, Product updated) {
        Product existing = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        existing.setProductName(updated.getProductName());
        existing.setDescription(updated.getDescription());
        existing.setBrand(updated.getBrand());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        existing.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(existing);
    }

    // Xóa sản phẩm cùng toàn bộ variant và image liên quan
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId));

        // Xóa variants trước
        List<ProductVariant> variants = productVariantRepository.findByProduct_ProductID(productId);
        productVariantRepository.deleteAll(variants);

        // Xóa images trước
        List<ProductImage> images = productImageRepository.findByProduct_ProductIDOrderByIsMainDesc(productId);
        productImageRepository.deleteAll(images);

        // Xóa product
        productRepository.delete(product);
    }

    public Page<ProductCardDTO> getProductsByCollection(String type, int page, int size) {
        if (size <= 0) size = 40;
        if (page < 0) page = 0;

        // Định nghĩa sắp xếp theo ID cột của Database cho Native Query
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ProductID"));
        Page<Product> productPage;

        switch (type.toLowerCase()) {
            case "best-sellers": productPage = productRepository.findBestSellers(pageable); break;
            case "international": productPage = productRepository.findInternational(pageable); break;
            case "womens-fashion": productPage = productRepository.findWomensFashion(pageable); break;
            case "mens-fashion": productPage = productRepository.findMensFashion(pageable); break; // Thay đổi giáo dục -> thời trang nam
            case "beauty": productPage = productRepository.findBeauty(pageable); break;
            case "electronics": productPage = productRepository.findElectronics(pageable); break;
            case "sports": productPage = productRepository.findSports(pageable); break;
            case "home-appliances": productPage = productRepository.findHomeAppliances(pageable); break;
            default: productPage = productRepository.findAll(pageable);
        }

        List<ProductCardDTO> cards = toCards(productPage.getContent());
        return new PageImpl<>(cards, pageable, productPage.getTotalElements());
    }

}