package com.webthuongmai.repository;
import com.webthuongmai.entity.Voucher;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findByShop_ShopID(Long shopId);
    List<Voucher> findByVoucherTypeAndStatus(String voucherType, String status);
}