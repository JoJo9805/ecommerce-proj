package com.webthuongmai.service;
import com.webthuongmai.entity.ProductVariant;
import com.webthuongmai.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductVariantService {
    @Autowired
    private ProductVariantRepository productVariantRepository;

    public List<ProductVariant> getAllProductVariants() {
        return productVariantRepository.findAll();
    }

    // Lấy danh sách variant theo sản phẩm
    public List<ProductVariant> getVariantsByProductId(Long productId) {
        return productVariantRepository.findByProduct_ProductID(productId);
    }

    public ProductVariant createProductVariant(ProductVariant productVariant) {
        return productVariantRepository.save(productVariant);
    }

    // Cập nhật variant theo ID
    @Transactional
    public ProductVariant updateProductVariant(Long variantId, ProductVariant updated) {
        ProductVariant existing = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy variant ID: " + variantId));
        existing.setSize(updated.getSize());
        existing.setColor(updated.getColor());
        existing.setPrice(updated.getPrice());
        existing.setStockQuantity(updated.getStockQuantity() != null ? updated.getStockQuantity() : existing.getStockQuantity());
        existing.setSku(updated.getSku());
        existing.setStatus(updated.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return productVariantRepository.save(existing);
    }

    // Xóa variant theo ID
    public void deleteProductVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy variant ID: " + variantId));
        productVariantRepository.delete(variant);
    }
}