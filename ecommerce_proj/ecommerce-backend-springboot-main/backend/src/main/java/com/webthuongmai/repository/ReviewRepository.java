package com.webthuongmai.repository;
import com.webthuongmai.entity.OrderItem;
import com.webthuongmai.entity.Review;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 1. Chỉ lấy danh sách Đánh giá SẠCH (isFake = false hoặc null)
    @Query("SELECT r FROM Review r WHERE r.product.productID = :productId AND (r.isFake = false OR r.isFake IS NULL)")
    List<Review> findByProduct_ProductID(@Param("productId") Long productId);

    Optional<Review> findByOrderItem(OrderItem orderItem);
    boolean existsByOrderItem_OrderItemID(Long orderItemId);

    // 2. Chỉ tính điểm trung bình sao cho những Đánh giá SẠCH
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productID = :productId AND (r.isFake = false OR r.isFake IS NULL)")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
}