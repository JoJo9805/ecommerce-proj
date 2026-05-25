package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "Vouchers")
@Data
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voucherID;

    private String voucherType;
    private BigDecimal discountValue;

    @Transient
    private BigDecimal minOrderValue;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;

    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime deletedAt;

    private Integer quantity = 0;
    private Integer remainingQuantity = 0;

    private String voucherCode;

    @ManyToOne
    @JoinColumn(name = "ShopID")
    private Shop shop;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}