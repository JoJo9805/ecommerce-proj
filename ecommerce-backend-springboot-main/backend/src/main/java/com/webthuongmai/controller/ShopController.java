package com.webthuongmai.controller;
import com.webthuongmai.entity.Shop;
import com.webthuongmai.repository.ShopRepository;
import com.webthuongmai.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.webthuongmai.dto.ShopProfileDTO;
import com.webthuongmai.dto.SellerRegisterRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin("*")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @GetMapping
    public List<Shop> getAll() {
        return shopService.getAllShops();
    }

    @Autowired
    private ShopRepository shopRepository;

    @GetMapping("/{id}")
    public ResponseEntity<ShopProfileDTO> getShopInfo(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shopService.getShopProfile(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Shop create(@RequestBody Shop shop) {
        return shopService.createShop(shop);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerSeller(@RequestBody SellerRegisterRequest request) {
        try {
            Shop registeredShop = shopService.registerSeller(request);
            return ResponseEntity.ok(registeredShop);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Shop> followShop(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam boolean isFollowing) {
        return ResponseEntity.ok(shopService.updateFollowerCount(id, userId, isFollowing));
    }

    // Thêm API này để check trạng thái lúc vào trang
    @GetMapping("/{id}/check-follow")
    public ResponseEntity<Boolean> checkFollowStatus(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(shopService.checkUserFollowStatus(id, userId));
    }

    @PutMapping("/{id}/description")
    public ResponseEntity<Shop> updateDescription(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String newDescription = body.get("description");
        return ResponseEntity.ok(shopService.updateDescription(id, newDescription));
    }
}