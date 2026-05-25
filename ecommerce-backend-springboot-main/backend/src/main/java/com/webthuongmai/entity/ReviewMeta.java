package com.webthuongmai.entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "Review_Metas")
@Data
public class ReviewMeta {
    @Id
    private Long reviewID;

    @OneToOne
    @MapsId
    @JoinColumn(name = "ReviewID")
    private Review review;

    @Column(name = "IP_Address")
    private String ipAddress;
    
    private String deviceID;
}