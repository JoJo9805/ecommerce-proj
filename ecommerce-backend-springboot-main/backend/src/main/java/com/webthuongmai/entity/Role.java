package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "Roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleID;

    private String roleName;
}