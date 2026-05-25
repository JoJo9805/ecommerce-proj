package com.webthuongmai.dto;

import lombok.Data;

@Data
public class SellerRegisterRequest {
    private Long userID;
    private String shopName;
    private String description;
}
