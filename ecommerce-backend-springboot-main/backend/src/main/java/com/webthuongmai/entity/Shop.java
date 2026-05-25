package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "Shops")
@Data
public class Shop {
    @Id
    private Long shopID;

    @OneToOne
    @MapsId
    @JoinColumn(name = "ShopID")
    private User user;

    @Column(nullable = false)
    private String shopName;
    
    private String description;
    private Double rating = 0.0;
    
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime deletedAt;

    @Column(name = "FollowerCount")
    private Integer followerCount = 0;

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    @Transient
    private Integer salesCount = 0;

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }
}