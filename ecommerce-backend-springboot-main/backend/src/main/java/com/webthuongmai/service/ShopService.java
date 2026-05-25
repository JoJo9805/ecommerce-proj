package com.webthuongmai.service;
import com.webthuongmai.entity.Shop;
import com.webthuongmai.entity.User;
import com.webthuongmai.entity.Role;
import com.webthuongmai.entity.ShopFollower;
import com.webthuongmai.repository.ShopRepository;
import com.webthuongmai.repository.UserRepository;
import com.webthuongmai.repository.RoleRepository;
import com.webthuongmai.repository.ProductRepository;
import com.webthuongmai.repository.ShopFollowerRepository;
import com.webthuongmai.repository.OrderItemRepository;
import com.webthuongmai.dto.ShopProfileDTO;
import com.webthuongmai.dto.SellerRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ShopFollowerRepository shopFollowerRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public Shop createShop(Shop shop) {
        return shopRepository.save(shop);
    }

    @Transactional
    public Shop registerSeller(SellerRegisterRequest request) {
        User user = userRepository.findById(request.getUserID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        // Check if shop already exists
        if (shopRepository.existsById(request.getUserID())) {
            throw new RuntimeException("Người dùng đã đăng ký Shop rồi");
        }

        // Change user role to Seller
        Role sellerRole = roleRepository.findAll().stream()
                .filter(r -> r.getRoleName().equalsIgnoreCase("Seller"))
                .findFirst()
                .orElseGet(() -> {
                    return roleRepository.findById(3L).orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleName("Seller");
                        return roleRepository.save(newRole);
                    });
                });

        user.setRole(sellerRole);
        userRepository.save(user);

        // Create Shop
        Shop shop = new Shop();
        shop.setUser(user);
        shop.setShopName(request.getShopName());
        shop.setDescription(request.getDescription());
        shop.setRating(0.0);
        shop.setFollowerCount(0);
        shop.setCreatedAt(LocalDateTime.now());
        shop.setUpdatedAt(LocalDateTime.now());

        return shopRepository.save(shop);
    }

    @Transactional
    public Shop updateFollowerCount(Long shopId, Long userId, boolean isFollowing) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        // Kiểm tra xem user này đã follow shop chưa
        boolean isAlreadyFollowing = shopFollowerRepository.existsByUser_UserIDAndShop_ShopID(userId, shopId);
        int currentCount = shop.getFollowerCount() != null ? shop.getFollowerCount() : 0;

        if (isFollowing) {
            // Chỉ tăng và lưu DB nếu chưa follow
            if (!isAlreadyFollowing) {
                ShopFollower follower = new ShopFollower();
                follower.setUser(user);
                follower.setShop(shop);
                shopFollowerRepository.save(follower);
                shop.setFollowerCount(currentCount + 1);
            }
        } else {
            // Chỉ giảm và xóa khỏi DB nếu đang follow
            if (isAlreadyFollowing) {
                shopFollowerRepository.deleteByUser_UserIDAndShop_ShopID(userId, shopId);
                shop.setFollowerCount(Math.max(0, currentCount - 1));
            }
        }
        return shopRepository.save(shop);
    }

    // Thêm hàm kiểm tra trạng thái cho Frontend
    public boolean checkUserFollowStatus(Long shopId, Long userId) {
        return shopFollowerRepository.existsByUser_UserIDAndShop_ShopID(userId, shopId);
    }

    public Shop updateFollowerCount(Long shopId, boolean isFollowing) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));

        int currentCount = shop.getFollowerCount() != null ? shop.getFollowerCount() : 0;

        if (isFollowing) {
            shop.setFollowerCount(currentCount + 1); // Tăng 1 nếu bấm theo dõi
        } else {
            shop.setFollowerCount(Math.max(0, currentCount - 1)); // Giảm 1 nếu bỏ theo dõi (không cho âm)
        }
        return shopRepository.save(shop);
    }

    public Shop updateDescription(Long shopId, String newDescription) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));
        shop.setDescription(newDescription);
        return shopRepository.save(shop);
    }

    public ShopProfileDTO getShopProfile(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));

        long productCount = productRepository.countByShop_ShopID(shopId);

        // SỬA: Lấy tổng số lượt bán thực tế từ Database thay vì mock data
        int salesCount = orderItemRepository.countSoldByShopId(shopId);
        shop.setSalesCount(salesCount);

        return new ShopProfileDTO(shop, productCount, salesCount);
    }
}