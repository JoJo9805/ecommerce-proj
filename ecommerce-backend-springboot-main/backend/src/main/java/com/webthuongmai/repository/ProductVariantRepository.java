package com.webthuongmai.repository;
import com.webthuongmai.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProduct_ProductID(Long productId);

    // Lấy biến thể của NHIỀU sản phẩm trong 1 query (tránh N+1)
    List<ProductVariant> findByProduct_ProductIDIn(Collection<Long> productIds);
}