package com.webthuongmai.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "User_Vouchers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"UserID", "VoucherID"})
})
public class UserVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "VoucherID")
    private Voucher voucher;

    @Column(name = "IsUsed")
    private Boolean isUsed = false;

    @Column(name = "SavedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date savedAt = new Date();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Voucher getVoucher() { return voucher; }
    public void setVoucher(Voucher voucher) { this.voucher = voucher; }

    public Boolean getIsUsed() { return isUsed; }
    public void setIsUsed(Boolean isUsed) { this.isUsed = isUsed; }

    public Date getSavedAt() { return savedAt; }
    public void setSavedAt(Date savedAt) { this.savedAt = savedAt; }
}