package com.webthuongmai.dto;

import lombok.Data;
import java.sql.Date;

@Data
public class UserProfileDTO {
    private String fullName;
    private String email;
    private String phone;
    private Date birthday;
    private String gender;
    private String password;
}