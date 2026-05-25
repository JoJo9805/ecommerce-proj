package com.webthuongmai.service;
import com.webthuongmai.entity.Cart;
import com.webthuongmai.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public Cart createCart(Cart cart) {
        return cartRepository.save(cart);
    }
}