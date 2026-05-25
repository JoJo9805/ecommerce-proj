package com.webthuongmai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "Shop_Followers")
@Data
@IdClass(ShopFollowerId.class)
public class ShopFollower {

    @Id
    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "ShopID")
    private Shop shop;

    @Column(name = "FollowedAt", updatable = false)
    private LocalDateTime followedAt = LocalDateTime.now();
}