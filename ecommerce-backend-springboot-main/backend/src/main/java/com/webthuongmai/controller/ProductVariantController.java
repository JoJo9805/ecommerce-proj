package com.webthuongmai.controller;
import com.webthuongmai.entity.ProductVariant;
import com.webthuongmai.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product-variants")
@CrossOrigin("*")
public class ProductVariantController {
    @Autowired
    private ProductVariantService productVariantService;

    // Lấy tất cả variant
    @GetMapping
    public List<ProductVariant> getAll() {
        return productVariantService.getAllProductVariants();
    }

    // Lấy tất cả variant của một sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVariant>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productVariantService.getVariantsByProductId(productId));
    }

    // Tạo mới variant (cho 1 sản phẩm cụ thể)
    @PostMapping
    public ResponseEntity<ProductVariant> create(@RequestBody ProductVariant productVariant) {
        return ResponseEntity.ok(productVariantService.createProductVariant(productVariant));
    }

    // Cập nhật variant theo ID
    @PutMapping("/{id}")
    public ResponseEntity<ProductVariant> update(
            @PathVariable Long id,
            @RequestBody ProductVariant productVariant) {
        try {
            return ResponseEntity.ok(productVariantService.updateProductVariant(id, productVariant));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa variant theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            productVariantService.deleteProductVariant(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}