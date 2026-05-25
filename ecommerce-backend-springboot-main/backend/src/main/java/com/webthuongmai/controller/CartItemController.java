package com.webthuongmai.controller;

import com.webthuongmai.entity.CartItem;
import com.webthuongmai.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
@CrossOrigin("*")
public class CartItemController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @GetMapping
    public List<CartItem> getAll() {
        return cartItemRepository.findAll();
    }

    // --- 2 API MỚI BỔ SUNG CHO GIỎ HÀNG REACT ---

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        CartItem item = cartItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy món hàng"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return ResponseEntity.ok("Cập nhật số lượng thành công");
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        cartItemRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ");
    }
}