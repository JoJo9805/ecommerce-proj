package com.webthuongmai.repository;
import com.webthuongmai.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_OrderID(Long orderId);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.productVariant.product.productID = :productId")
    Integer countSoldByProductId(@Param("productId") Long productId);

    // TÍNH TỔNG SỐ LƯỢNG ĐÃ BÁN CỦA TẤT CẢ SẢN PHẨM THUỘC 1 SHOP
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.productVariant.product.shop.shopID = :shopId")
    Integer countSoldByShopId(@Param("shopId") Long shopId);
}