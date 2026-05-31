package com.webthuongmai.controller;
import com.webthuongmai.entity.ProductImage;
import com.webthuongmai.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@CrossOrigin("*")
public class ProductImageController {
    @Autowired
    private ProductImageService productImageService;

    // Lấy tất cả ảnh
    @GetMapping
    public List<ProductImage> getAll() {
        return productImageService.getAllProductImages();
    }

    // Lấy tất cả ảnh của một sản phẩm (sorted: ảnh chính lên đầu)
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImage>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getImagesByProductId(productId));
    }

    // Thêm ảnh mới cho sản phẩm
    @PostMapping
    public ResponseEntity<ProductImage> create(@RequestBody ProductImage productImage) {
        return ResponseEntity.ok(productImageService.createProductImage(productImage));
    }

    // Cập nhật ảnh theo ID (URL hoặc isMain)
    @PutMapping("/{id}")
    public ResponseEntity<ProductImage> update(
            @PathVariable Long id,
            @RequestBody ProductImage productImage) {
        try {
            return ResponseEntity.ok(productImageService.updateProductImage(id, productImage));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa ảnh theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            productImageService.deleteProductImage(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}