package com.webthuongmai.repository;

import com.webthuongmai.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("""
        SELECT DISTINCT ci FROM CartItem ci
        JOIN FETCH ci.cart c
        JOIN FETCH ci.productVariant pv
        JOIN FETCH pv.product p
        JOIN FETCH p.shop
        LEFT JOIN FETCH p.images
        WHERE c.user.userID = :userId
        ORDER BY ci.cartItemID DESC
    """)
    List<CartItem> findCartItemsByUser(@Param("userId") Long userId);

    Optional<CartItem> findByCart_CartIDAndProductVariant_VariantID(Long cartId, Long variantId);
}