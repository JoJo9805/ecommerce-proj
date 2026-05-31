package com.webthuongmai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productID;

    @Column(nullable = false)
    private String productName;

    private Double rating = 0.0;

    // @Transient giúp Spring biết cột này không có trong CSDL, chỉ dùng để tính toán tạm thời
    @Transient
    private Integer soldCount = 0;

    // Trả về đúng bản chất quan hệ Nhiều-Nhiều của bảng Product_Categories_Map
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Product_Categories_Map",
            joinColumns = @JoinColumn(name = "ProductID"),
            inverseJoinColumns = @JoinColumn(name = "CategoryID")
    )
    private List<Category> categories = new ArrayList<>();

    // Giúp các file khác vẫn gọi getCategory() bình thường mà không bị báo lỗi đỏ code
    public Category getCategory() {
        if (this.categories != null && !this.categories.isEmpty()) {
            return this.categories.get(0);
        }
        return null;
    }

    public void setCategory(Category category) {
        if (this.categories == null) {
            this.categories = new ArrayList<>();
        }
        this.categories.clear();
        if (category != null) {
            this.categories.add(category);
        }
    }

    @ManyToOne
    @JoinColumn(name = "ShopID")
    private Shop shop;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> images;

    private String description;
    private String brand;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime deletedAt;

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    @Transient
    private Integer shopTotalSales = 0;

    @Transient
    private Long shopProductCount = 0L;

    public Integer getShopTotalSales() {
        return shopTotalSales;
    }

    public void setShopTotalSales(Integer shopTotalSales) {
        this.shopTotalSales = shopTotalSales;
    }

    public Long getShopProductCount() {
        return shopProductCount;
    }

    public void setShopProductCount(Long shopProductCount) {
        this.shopProductCount = shopProductCount;
    }
}