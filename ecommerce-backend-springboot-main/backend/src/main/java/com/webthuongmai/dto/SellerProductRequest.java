package com.webthuongmai.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO dành riêng cho Seller khi tạo mới hoặc cập nhật sản phẩm.
 * Gộp thông tin Product + danh sách Variant + danh sách Image URL
 * vào một request duy nhất, tránh phải gọi nhiều API rời rạc.
 */
public class SellerProductRequest {

    // ====== Thông tin cơ bản của Product ======
    private String productName;
    private String description;
    private String brand;
    private Long categoryId;   // CategoryID để set category
    private Long shopId;       // ShopID (bắt buộc khi tạo mới)

    // ====== Danh sách Variant ======
    private List<VariantDTO> variants;

    // ====== Danh sách Image URL ======
    private List<ImageDTO> images;

    // ===== Getters & Setters =====

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public List<VariantDTO> getVariants() { return variants; }
    public void setVariants(List<VariantDTO> variants) { this.variants = variants; }

    public List<ImageDTO> getImages() { return images; }
    public void setImages(List<ImageDTO> images) { this.images = images; }

    // ====== Inner DTO: Variant ======
    public static class VariantDTO {
        private Long variantID;      // null khi tạo mới, có giá trị khi cập nhật
        private String size;
        private String color;
        private BigDecimal price;
        private Integer stockQuantity;
        private String sku;
        private String status;

        public Long getVariantID() { return variantID; }
        public void setVariantID(Long variantID) { this.variantID = variantID; }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // ====== Inner DTO: Image ======
    public static class ImageDTO {
        private Long imageID;        // null khi tạo mới, có giá trị khi cập nhật
        private String imageURL;
        private Boolean isMain;

        public Long getImageID() { return imageID; }
        public void setImageID(Long imageID) { this.imageID = imageID; }

        public String getImageURL() { return imageURL; }
        public void setImageURL(String imageURL) { this.imageURL = imageURL; }

        public Boolean getIsMain() { return isMain; }
        public void setIsMain(Boolean isMain) { this.isMain = isMain; }
    }
}
