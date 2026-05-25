package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "Orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderID;

    @ManyToOne
    @JoinColumn(name = "BuyerID")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "ShopID")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "AddressID")
    private Address address;

    @Column(updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();
    
    private String paymentStatus = "Unpaid";
    private String shippingStatus = "Pending";

    @Transient
    private String paymentMethod;

    @Transient
    private LocalDateTime expectedDeliveryDate;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}