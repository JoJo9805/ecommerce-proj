package com.webthuongmai.dto;

import com.webthuongmai.entity.Product;
import com.webthuongmai.entity.ProductImage;
import com.webthuongmai.entity.ProductVariant;
import com.webthuongmai.entity.Review;

import java.util.List;

public class ProductDetailDTO {
    private Product product;
    private List<ProductVariant> variants;
    private List<ProductImage> images;
    private List<Review> reviews;

    public ProductDetailDTO() {}

    public ProductDetailDTO(Product product, List<ProductVariant> variants, List<ProductImage> images, List<Review> reviews) {
        this.product = product;
        this.variants = variants;
        this.images = images;
        this.reviews = reviews;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }

    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}