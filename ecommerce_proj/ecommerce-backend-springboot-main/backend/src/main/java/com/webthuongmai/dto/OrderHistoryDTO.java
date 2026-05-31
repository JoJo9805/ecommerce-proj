package com.webthuongmai.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lịch sử một đơn hàng của người mua, kèm danh sách sản phẩm trong đơn.
 * Trả cho trang "Lịch sử đặt hàng" ở Account.
 */
public class OrderHistoryDTO {
    private Long orderID;
    private String shopName;
    private Long shopID;
    private LocalDateTime orderDate;
    private String shippingStatus;
    private String paymentStatus;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private List<Item> items;

    public OrderHistoryDTO() {}

    public static class Item {
        private Long orderItemID;
        private Long variantID;
        private Long productID;
        private String productName;
        private String size;
        private String color;
        private Integer quantity;
        private BigDecimal price;
        private String imageURL;
        private Boolean reviewed = false;   // đã đánh giá chưa
        private Integer rating;             // điểm đã đánh giá (nếu có)
        private String comment;             // nội dung đánh giá (nếu có)
        private String reviewDate;          // ngày đánh giá dd/MM/yyyy (nếu có)

        public Long getOrderItemID() { return orderItemID; }
        public void setOrderItemID(Long orderItemID) { this.orderItemID = orderItemID; }

        public Long getVariantID() { return variantID; }
        public void setVariantID(Long variantID) { this.variantID = variantID; }

        public Long getProductID() { return productID; }
        public void setProductID(Long productID) { this.productID = productID; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public String getImageURL() { return imageURL; }
        public void setImageURL(String imageURL) { this.imageURL = imageURL; }

        public Boolean getReviewed() { return reviewed; }
        public void setReviewed(Boolean reviewed) { this.reviewed = reviewed; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public String getReviewDate() { return reviewDate; }
        public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
    }

    public Long getOrderID() { return orderID; }
    public void setOrderID(Long orderID) { this.orderID = orderID; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public Long getShopID() { return shopID; }
    public void setShopID(Long shopID) { this.shopID = shopID; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getShippingStatus() { return shippingStatus; }
    public void setShippingStatus(String shippingStatus) { this.shippingStatus = shippingStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}