package com.webthuongmai.repository;

import com.webthuongmai.entity.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {
    boolean existsByUser_UserIDAndVoucher_VoucherID(Long userId, Long voucherId);

    List<UserVoucher> findByUser_UserID(Long userId);
}