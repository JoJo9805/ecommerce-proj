package com.webthuongmai.dto;

import java.math.BigDecimal;

/**
 * DTO gọn nhẹ dùng cho danh sách sản phẩm ở trang chủ và trang tìm kiếm.
 * Mỗi sản phẩm trả kèm: ảnh đại diện, giá thấp nhất và variantID tương ứng
 * (để nút "Add to Cart" gửi đúng mã biến thể), cùng rating của shop.
 */
public class ProductCardDTO {
    private Long productID;
    private String productName;
    private String brand;
    private String imageURL;     // ảnh chính (hoặc null nếu chưa có ảnh)
    private BigDecimal price;    // giá thấp nhất trong các biến thể (hoặc null nếu chưa có biến thể)
    private Long variantID;      // mã biến thể rẻ nhất để thêm vào giỏ (hoặc null nếu chưa có biến thể)
    private Double shopRating;   // điểm đánh giá của shop
    private Long shopID;
    private Long categoryID;
    private Double rating;
    private Integer soldCount;

    public ProductCardDTO() {}

    public Long getProductID() { return productID; }
    public void setProductID(Long productID) { this.productID = productID; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Long getVariantID() { return variantID; }
    public void setVariantID(Long variantID) { this.variantID = variantID; }

    public Double getShopRating() { return shopRating; }
    public void setShopRating(Double shopRating) { this.shopRating = shopRating; }

    public Long getShopID() { return shopID; }
    public void setShopID(Long shopID) { this.shopID = shopID; }

    public Long getCategoryID() { return categoryID; }
    public void setCategoryID(Long categoryID) { this.categoryID = categoryID; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }
}