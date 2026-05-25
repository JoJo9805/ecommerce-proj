package com.webthuongmai.repository;
import com.webthuongmai.entity.OrderItem;
import com.webthuongmai.entity.Review;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_ProductID(Long productId);
    Optional<Review> findByOrderItem(OrderItem orderItem);
    boolean existsByOrderItem_OrderItemID(Long orderItemId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productID = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
}