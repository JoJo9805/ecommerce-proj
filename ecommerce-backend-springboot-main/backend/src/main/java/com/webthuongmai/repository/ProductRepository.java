package com.webthuongmai.repository;
import com.webthuongmai.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductNameContainingIgnoreCase(String name);

    List<Product> findByCategory_CategoryID(Long categoryId);

    List<Product> findByProductNameContainingIgnoreCaseAndCategory_CategoryID(String name, Long categoryId);

    List<Product> findByCategory_CategoryIDAndProductIDNot(Long categoryId, Long productId);

    // ===== Phiên bản PHÂN TRANG cho danh sách / tìm kiếm trang chủ =====
    Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByCategory_CategoryID(Long categoryId, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndCategory_CategoryID(String name, Long categoryId, Pageable pageable);

    // Query lấy sản phẩm Trending dựa trên số lượt tương tác trong bảng user_activities
    // Lưu ý: Các tên trường (productid, productName...) phải khớp chính xác với file Product Entity
    @Query("SELECT p FROM Product p JOIN UserActivity ua ON p.productID = ua.product.productID " +
            "GROUP BY p " +
            "ORDER BY COUNT(ua) DESC")
    List<Product> findTrendingProducts(Pageable pageable);

    List<Product> findByShop_ShopIDAndProductIDNot(Long shopId, Long productId);

    List<Product> findByShop_ShopID(Long shopId);

    long countByShop_ShopID(Long shopId);

    // 1. Tìm kiếm phân trang (Không theo category) - Đã đổi sang dùng OR
    @Query("""
        SELECT DISTINCT p FROM Product p
        WHERE 
            LOWER(p.productName) LIKE :w1 
            OR LOWER(p.brand) LIKE :w1
            OR (:w2 IS NOT NULL AND (LOWER(p.productName) LIKE :w2 OR LOWER(p.brand) LIKE :w2))
    """)
    Page<Product> searchProductsAdvanced(@Param("w1") String w1, @Param("w2") String w2, Pageable pageable);

    // 2. Tìm kiếm không phân trang (Không theo category) - Đã đổi sang dùng OR
    @Query("""
        SELECT DISTINCT p FROM Product p
        WHERE 
            LOWER(p.productName) LIKE :w1 
            OR LOWER(p.brand) LIKE :w1
            OR (:w2 IS NOT NULL AND (LOWER(p.productName) LIKE :w2 OR LOWER(p.brand) LIKE :w2))
    """)
    List<Product> searchProductsAdvanced(@Param("w1") String w1, @Param("w2") String w2);

    // 3. Tìm kiếm phân trang (Có kèm theo category) - Đã đổi sang dùng OR
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE " +
            "(LOWER(p.productName) LIKE :w1 OR LOWER(p.brand) LIKE :w1 " +
            "OR (:w2 IS NOT NULL AND (LOWER(p.productName) LIKE :w2_1 OR LOWER(p.brand) LIKE :w2_1))) " +
            "AND c.categoryID = :categoryId")
    Page<Product> searchProductsByCategoryAdvanced(@Param("w1") String w1, @Param("w2") String w2, @Param("categoryId") Long categoryId, Pageable pageable);

    // 4. Tìm kiếm không phân trang (Có kèm theo category) - Đã đổi sang dùng OR
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE " +
            "(LOWER(p.productName) LIKE :w1 OR LOWER(p.brand) LIKE :w1 " +
            "OR (:w2 IS NOT NULL AND (LOWER(p.productName) LIKE :w2_1 OR LOWER(p.brand) LIKE :w2_1))) " +
            "AND c.categoryID = :categoryId")
    List<Product> searchProductsByCategoryAdvanced(@Param("w1") String w1, @Param("w2") String w2, @Param("categoryId") Long categoryId);

    @Query(value = "SELECT p.* FROM Products p JOIN UI_Best_Sellers u ON p.ProductID = u.ProductID",
            countQuery = "SELECT COUNT(*) FROM UI_Best_Sellers", nativeQuery = true)
    Page<Product> findBestSellers(Pageable pageable);

    @Query(value = "SELECT p.* FROM Products p JOIN UI_International u ON p.ProductID = u.ProductID",
            countQuery = "SELECT COUNT(*) FROM UI_International", nativeQuery = true)
    Page<Product> findInternational(Pageable pageable);

    @Query(value = "SELECT p.* FROM Products p JOIN UI_Womens_Fashion u ON p.ProductID = u.ProductID",
            countQuery = "SELECT COUNT(*) FROM UI_Womens_Fashion", nativeQuery = true)
    Page<Product> findWomensFashion(Pageable pageable);

    @Query(value = "SELECT p.* FROM Products p JOIN UI_Mens_Fashion u ON p.ProductID = u.ProductID",
            countQuery = "SELECT COUNT(*) FROM UI_Mens_Fashion", nativeQuery = true)
    Page<Product> findMensFashion(Pageable pageable);

    @Query(value = "SELECT p.* FROM Products p JOIN UI_Beauty u ON p.ProductID = u.ProductID",
            countQuery = "SELECT COUNT(*) FROM UI_Beauty", nativeQuery = true)
    Page<Product> findBeauty(Pageable pageable);

    @Query(value = "SELECT p.* FROM Products p JOIN UI_Electronics u ON p.ProductID = u.ProductID",
            countQuery = "SELECT COUNT(*) FROM UI_Electronics", nativeQuery = true)
    Page<Product> findElectronics(Pageable pageable);

    @Query(value = "SELECT p.* FROM Products p JOIN UI_Sports u ON p.ProductID = u.ProductID",
            countQuery = "SELECT COUNT(*) FROM UI_Sports", nativeQuery = true)
    Page<Product> findSports(Pageable pageable);

    @Query(value = "SELECT p.* FROM Products p JOIN UI_Home_Appliances u ON p.ProductID = u.ProductID",
            countQuery = "SELECT COUNT(*) FROM UI_Home_Appliances", nativeQuery = true)
    Page<Product> findHomeAppliances(Pageable pageable);
}