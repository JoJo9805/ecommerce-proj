package com.webthuongmai.dto;
import lombok.Data;
import java.sql.Date;
@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
    private Date birthday;
    private String gender;
}