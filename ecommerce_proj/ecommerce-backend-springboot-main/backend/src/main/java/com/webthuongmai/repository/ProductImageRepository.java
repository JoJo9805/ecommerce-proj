package com.webthuongmai.repository;
import com.webthuongmai.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // Tất cả ảnh của một sản phẩm (ảnh chính được ưu tiên lên đầu)
    List<ProductImage> findByProduct_ProductIDOrderByIsMainDesc(Long productId);

    // Lấy ảnh của NHIỀU sản phẩm trong 1 query (tránh N+1)
    List<ProductImage> findByProduct_ProductIDInOrderByIsMainDesc(Collection<Long> productIds);
}