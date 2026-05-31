package com.webthuongmai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "Users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    private java.sql.Date birthday;

    private String gender;

    private Integer followerCount = 0;

    private String status = "Active";
    private LocalDateTime lastLoginDate;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime deletedAt;

    // FIX: Đổi từ @ManyToOne sang @ManyToMany để tránh lỗi Duplicate row
    // khi bảng trung gian User_Roles trả về nhiều hơn 1 bản ghi.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "User_Roles",
            joinColumns = @JoinColumn(name = "UserID"),
            inverseJoinColumns = @JoinColumn(name = "RoleID")
    )
    private List<Role> roles = new ArrayList<>();

    // Giữ lại các method get/set để không ảnh hưởng đến logic ở các file Service/Controller khác
    public Role getRole() {
        if (this.roles != null && !this.roles.isEmpty()) {
            return this.roles.get(0);
        }
        return null;
    }

    public void setRole(Role role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.clear();
        if (role != null) {
            this.roles.add(role);
        }
    }
}