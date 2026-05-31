package com.webthuongmai.service;
import com.webthuongmai.entity.UserVoucher;
import com.webthuongmai.entity.Voucher;
import com.webthuongmai.entity.User;
import com.webthuongmai.repository.UserRepository;
import com.webthuongmai.repository.UserVoucherRepository;
import com.webthuongmai.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserVoucherRepository userVoucherRepository;

    @Autowired
    private NotificationService notificationService;


    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Voucher createVoucher(Voucher voucher) {
        if (voucher.getQuantity() != null && voucher.getRemainingQuantity() == null || voucher.getRemainingQuantity() == 0) {
            voucher.setRemainingQuantity(voucher.getQuantity());
        }
        return voucherRepository.save(voucher);
    }

    public String saveVoucherToWallet(Long userId, Long voucherId) {
        if (userVoucherRepository.existsByUser_UserIDAndVoucher_VoucherID(userId, voucherId)) {
            return "Voucher này đã có trong ví của bạn!";
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        // Kiểm tra số lượng còn lại (Giờ Java đã đọc được từ SQL)
        if (voucher.getRemainingQuantity() != null && voucher.getRemainingQuantity() <= 0) {
            return "Voucher đã hết lượt sử dụng!";
        }

        // Kiểm tra hạn dùng
        if (voucher.getEndDate() != null && voucher.getEndDate().isBefore(java.time.LocalDateTime.now())) {
            return "Voucher đã hết hạn!";
        }

        UserVoucher uv = new UserVoucher();
        uv.setUser(user);
        uv.setVoucher(voucher);
        uv.setIsUsed(false);
        userVoucherRepository.save(uv);

        // THÊM MỚI: Trừ đi 1 lượt sử dụng của Voucher và lưu lại vào DB
        if (voucher.getRemainingQuantity() != null) {
            voucher.setRemainingQuantity(voucher.getRemainingQuantity() - 1);
            voucherRepository.save(voucher);
        }

        // Thông báo lưu voucher thành công
        try {
            notificationService.create(
                    userId,
                    "VOUCHER",
                    "Lưu mã giảm giá",
                    "Bạn vừa lưu một mã giảm giá vào ví. Hãy dùng khi thanh toán để được ưu đãi nhé!",
                    "/account"
            );
        } catch (Exception e) {
            System.err.println("Không tạo được thông báo voucher: " + e.getMessage());
        }

        return "Lưu Voucher thành công!";
    }

    public List<Voucher> getVouchersByType(String type) {
        return voucherRepository.findByVoucherTypeAndStatus(type, "Active");
    }

    // Lấy danh sách voucher mà user đã lưu vào ví
    public List<Voucher> getSavedVouchers(Long userId) {
        List<UserVoucher> userVouchers = userVoucherRepository.findByUser_UserID(userId);
        return userVouchers.stream()
                .map(UserVoucher::getVoucher)
                .toList();
    }

}