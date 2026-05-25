package com.webthuongmai.dto;

import com.webthuongmai.entity.Order;
import com.webthuongmai.entity.OrderItem;

import java.util.List;

public class ShopOrderDTO {
    private Order order;
    private List<OrderItem> items;

    public ShopOrderDTO() {}

    public ShopOrderDTO(Order order, List<OrderItem> items) {
        this.order = order;
        this.items = items;
    }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
