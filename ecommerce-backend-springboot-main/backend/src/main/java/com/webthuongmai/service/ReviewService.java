package com.webthuongmai.service;
import com.webthuongmai.dto.ReviewRequestDTO;
import com.webthuongmai.entity.Product;
import com.webthuongmai.entity.OrderItem;
import com.webthuongmai.entity.Review;
import com.webthuongmai.entity.User;
import com.webthuongmai.repository.ProductRepository;
import com.webthuongmai.repository.OrderItemRepository;
import com.webthuongmai.repository.ReviewRepository;
import com.webthuongmai.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private NotificationService notificationService;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public String createReview(ReviewRequestDTO request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Sản phẩm"));

        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đơn hàng"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setOrderItem(orderItem);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setReviewDate(LocalDateTime.now());
        review.setIsFake(false);

        review = reviewRepository.save(review);

        // Gọi ML API kiểm tra fake review
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("review_id", review.getReviewID());
            requestBody.put("comment", review.getComment());
            requestBody.put("rating", review.getRating());
            requestBody.put("has_order", 1);
            requestBody.put("ip_frequency", 1);
            requestBody.put("device_frequency", 1);

            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.postForEntity(
                    "http://localhost:8000/api/ai/reviews/moderate-review",
                    requestBody,
                    java.util.Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Boolean isFake = (Boolean) response.getBody().get("is_fake");
                if (isFake != null && isFake) {
                    review.setIsFake(true);
                    reviewRepository.save(review);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi gọi ML API: " + e.getMessage());
        }

        // Thông báo đánh giá thành công
        try {
            notificationService.create(
                    user.getUserID(),
                    "REVIEW",
                    "Đánh giá thành công",
                    "Cảm ơn bạn đã đánh giá sản phẩm \"" + product.getProductName() + "\" với " + request.getRating() + " sao!",
                    "/account/order"
            );
        } catch (Exception e) {
            System.err.println("Không tạo được thông báo đánh giá: " + e.getMessage());
        }

        return "Gửi đánh giá thành công!";
    }

    // Cập nhật phản hồi của Shop cho một đánh giá
    public Review updateShopReply(Long reviewId, String shopReply) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));
        review.setShopReply(shopReply);
        review.setUpdatedAt(java.time.LocalDateTime.now());
        return reviewRepository.save(review);
    }
}