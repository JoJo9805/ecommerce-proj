package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "Reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewID;

    @ManyToOne
    @JoinColumn(name = "ProductID")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "OrderItemID")
    private OrderItem orderItem;

    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(updatable = false)
    private LocalDateTime reviewDate = LocalDateTime.now();
    
    private Boolean isFake = false;
    
    @Column(columnDefinition = "TEXT")
    private String shopReply; // Phản hồi của shop

    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime deletedAt;
}