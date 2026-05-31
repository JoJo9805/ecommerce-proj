package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Carts")
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartID;

    @OneToOne
    @JoinColumn(name = "UserID")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("cart")
    private java.util.List<CartItem> cartItems = new java.util.ArrayList<>();
}