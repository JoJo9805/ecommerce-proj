package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Cart_Items")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemID;

    @ManyToOne
    @JoinColumn(name = "CartID")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "VariantID")
    private ProductVariant productVariant;

    private Integer quantity = 1;
}