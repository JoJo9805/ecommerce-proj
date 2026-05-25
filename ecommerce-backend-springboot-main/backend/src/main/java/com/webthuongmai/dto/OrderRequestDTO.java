package com.webthuongmai.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrderRequestDTO {
    private Long userId;
    private Long addressId;          // tùy chọn: dùng địa chỉ có sẵn
    private Long shopId;             // tùy chọn: nếu null sẽ tự suy từ giỏ hàng
    private List<Long> cartItemIds;
    private String paymentMethod;

    // Thông tin nhận hàng người dùng nhập trực tiếp ở form (ưu tiên hơn addressId)
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
}