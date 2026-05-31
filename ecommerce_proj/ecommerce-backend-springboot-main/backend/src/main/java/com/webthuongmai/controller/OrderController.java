package com.webthuongmai.controller;
import com.webthuongmai.dto.OrderDetailModalDTO;
import com.webthuongmai.dto.OrderHistoryDTO;
import com.webthuongmai.dto.OrderRequestDTO;
import com.webthuongmai.dto.ShopOrderDTO;
import com.webthuongmai.entity.Order;
import com.webthuongmai.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAll() {
        return orderService.getAllOrders();
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<Map<String, String>>> getPaymentMethods() {
        List<Map<String, String>> methods = Arrays.asList(
                Map.of("id", "COD", "name", "Thanh toán khi nhận hàng (COD)"),
                Map.of("id", "MOMO", "name", "Ví điện tử MoMo"),
                Map.of("id", "BANK", "name", "Chuyển khoản ngân hàng")
        );
        return ResponseEntity.ok(methods);
    }

    @GetMapping("/estimate-delivery")
    public ResponseEntity<Map<String, String>> getEstimateDelivery() {
        LocalDate fromDate = LocalDate.now().plusDays(3);
        LocalDate toDate = LocalDate.now().plusDays(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        Map<String, String> result = new HashMap<>();
        result.put("estimateText", "Đảm bảo nhận hàng vào ngày " + fromDate.format(formatter) + " - " + toDate.format(formatter));

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public Order create(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequestDTO request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrderHistoryDTO(userId));
    }

    @GetMapping("/items/{orderItemId}/details")
    public ResponseEntity<OrderDetailModalDTO> getOrderItemDetails(@PathVariable Long orderItemId) {
        return ResponseEntity.ok(orderService.getOrderItemDetails(orderItemId));
    }

    @PutMapping("/{orderId}/receive")
    public ResponseEntity<String> receiveOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.markOrderAsReceived(orderId));
    }

    // Hiển thị danh sách hóa đơn của Shop
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<java.util.List<ShopOrderDTO>> getShopOrders(@PathVariable Long shopId) {
        return ResponseEntity.ok(orderService.getShopOrders(shopId));
    }
}