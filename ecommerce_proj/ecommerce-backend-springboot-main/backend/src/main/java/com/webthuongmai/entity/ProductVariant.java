package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "Product_Variants")
@Data
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long variantID;

    @ManyToOne
    @JoinColumn(name = "ProductID")
    private Product product;

    private String size;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    private String color;
    private String status;
    private Integer stockQuantity = 0;
    
    @Column(unique = true)
    private String sku;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime deletedAt;
}