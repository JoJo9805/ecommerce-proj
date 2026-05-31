package com.webthuongmai.dto;

/**
 * Body JSON cho API POST /api/carts/add
 * { "userId": 1, "variantId": 5, "quantity": 1 }
 */
public class AddToCartRequest {
    private Long userId;
    private Long variantId;
    private Integer quantity = 1;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}