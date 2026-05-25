package com.webthuongmai.dto;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private Long userId;
    private Long productId;
    private Long orderItemId;
    private Integer rating;
    private String comment;
}