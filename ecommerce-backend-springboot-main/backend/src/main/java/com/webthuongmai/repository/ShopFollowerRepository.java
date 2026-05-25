package com.webthuongmai.repository;

import com.webthuongmai.entity.ShopFollower;
import com.webthuongmai.entity.ShopFollowerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopFollowerRepository extends JpaRepository<ShopFollower, ShopFollowerId> {
    boolean existsByUser_UserIDAndShop_ShopID(Long userId, Long shopId);
    void deleteByUser_UserIDAndShop_ShopID(Long userId, Long shopId);
}