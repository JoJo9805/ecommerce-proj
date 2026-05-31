package com.webthuongmai.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class OrderDetailModalDTO {
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String status;
    private LocalDateTime orderDate;
    private String reviewComment;
    private LocalDateTime reviewDate;
    private Integer reviewRating;
}