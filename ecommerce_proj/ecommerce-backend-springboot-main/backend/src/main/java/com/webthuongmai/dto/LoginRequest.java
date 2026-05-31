package com.webthuongmai.dto;
import lombok.Data;
@Data
public class LoginRequest {
    private String emailOrPhone;
    private String password;
}