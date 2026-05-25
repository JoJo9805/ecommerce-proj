package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "Shipping_Units")
@Data
public class ShippingUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipmentID;

    @ManyToOne
    @JoinColumn(name = "OrderID")
    private Order order;

    private String shippingMethod;
    private BigDecimal shippingFee;
    private String trackingNumber;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveryDate;
}