package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "Search_History")
@Data
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchID;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    private String keyword;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}