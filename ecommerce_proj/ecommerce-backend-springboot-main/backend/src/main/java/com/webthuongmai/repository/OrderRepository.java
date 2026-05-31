package com.webthuongmai.repository;
import com.webthuongmai.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer_UserIDOrderByOrderDateDesc(Long userId);
    List<Order> findByShop_ShopIDOrderByOrderDateDesc(Long shopId);
}