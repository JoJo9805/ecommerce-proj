package com.webthuongmai.service;
import com.webthuongmai.entity.ProductImage;
import com.webthuongmai.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductImageService {
    @Autowired
    private ProductImageRepository productImageRepository;

    public List<ProductImage> getAllProductImages() {
        return productImageRepository.findAll();
    }

    // Lấy ảnh theo sản phẩm (sorted: isMain DESC)
    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageRepository.findByProduct_ProductIDOrderByIsMainDesc(productId);
    }

    public ProductImage createProductImage(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    // Cập nhật ảnh theo ID (có thể đổi URL hoặc isMain)
    public ProductImage updateProductImage(Long imageId, ProductImage updated) {
        ProductImage existing = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh ID: " + imageId));
        existing.setImageURL(updated.getImageURL());
        existing.setIsMain(updated.getIsMain());
        return productImageRepository.save(existing);
    }

    // Xóa ảnh theo ID
    public void deleteProductImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh ID: " + imageId));
        productImageRepository.delete(image);
    }
}