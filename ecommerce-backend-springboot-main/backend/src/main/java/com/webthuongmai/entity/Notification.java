package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationID;

    @ManyToOne
    @JoinColumn(name = "SenderID")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "ReceiverID")
    private User receiver;

    private String type;
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String relatedLink;
    private Boolean isRead = false;
    
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime readAt;
}