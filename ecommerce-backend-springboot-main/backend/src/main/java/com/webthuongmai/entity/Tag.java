package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Tags")
@Data
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagID;

    @Column(nullable = false, unique = true)
    private String tagName;
}