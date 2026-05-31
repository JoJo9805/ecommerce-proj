package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "User_Roles")
@Data
public class UserRole {
    @Id
    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "RoleID")
    private Role role;
}