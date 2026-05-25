package com.webthuongmai.service;

import com.webthuongmai.dto.OrderDetailModalDTO;
import com.webthuongmai.dto.OrderHistoryDTO;
import com.webthuongmai.dto.OrderRequestDTO;
import com.webthuongmai.dto.ShopOrderDTO;
import com.webthuongmai.entity.CartItem;
import com.webthuongmai.entity.Order;
import com.webthuongmai.entity.OrderItem;
import com.webthuongmai.entity.ProductImage;
import com.webthuongmai.entity.ProductVariant;
import com.webthuongmai.entity.Review;
import com.webthuongmai.entity.User;
import com.webthuongmai.entity.Address;
import com.webthuongmai.entity.Shop;
import com.webthuongmai.repository.CartItemRepository;
import com.webthuongmai.repository.OrderItemRepository;
import com.webthuongmai.repository.OrderRepository;
import com.webthuongmai.repository.ProductImageRepository;
import com.webthuongmai.repository.ReviewRepository;
import com.webthuongmai.repository.UserRepository;
import com.webthuongmai.repository.AddressRepository;
import com.webthuongmai.repository.ShopRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private ShopRepository shopRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ProductImageRepository productImageRepository;
    @Autowired private NotificationService notificationService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public String placeOrder(OrderRequestDTO request) {

        User buyer = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        // Lấy các sản phẩm được chọn trong giỏ
        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào để đặt hàng");
        }

        // Xác định địa chỉ giao hàng:
        // 1) Nếu người dùng nhập SĐT + địa chỉ ở form -> tạo địa chỉ mới và lưu
        // 2) Ngược lại, dùng addressId có sẵn
        Address address;
        if (request.getReceiverAddress() != null && !request.getReceiverAddress().isBlank()) {
            address = new Address();
            address.setUser(buyer);
            address.setReceiverName(request.getReceiverName() != null ? request.getReceiverName() : buyer.getFullName());
            address.setPhone(request.getReceiverPhone());
            address.setDetailAddress(request.getReceiverAddress());
            address.setIsDefault(false);
            address = addressRepository.save(address);
        } else if (request.getAddressId() != null) {
            address = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Address"));
        } else {
            throw new RuntimeException("Thiếu thông tin địa chỉ nhận hàng");
        }

        final Address shippingAddress = address;

        // Gom các sản phẩm theo Shop (mỗi shop tạo 1 đơn hàng riêng)
        Map<Long, List<CartItem>> itemsByShop = new LinkedHashMap<>();
        for (CartItem item : cartItems) {
            Shop itemShop = null;
            if (item.getProductVariant() != null
                    && item.getProductVariant().getProduct() != null) {
                itemShop = item.getProductVariant().getProduct().getShop();
            }
            Long shopId = itemShop != null ? itemShop.getShopID()
                    : (request.getShopId() != null ? request.getShopId() : null);
            if (shopId == null) {
                throw new RuntimeException("Không xác định được Shop của sản phẩm");
            }
            itemsByShop.computeIfAbsent(shopId, k -> new ArrayList<>()).add(item);
        }

        int orderCount = 0;
        for (Map.Entry<Long, List<CartItem>> entry : itemsByShop.entrySet()) {
            Shop shop = shopRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));

            Order newOrder = new Order();
            newOrder.setBuyer(buyer);
            newOrder.setAddress(shippingAddress);
            newOrder.setShop(shop);
            newOrder.setOrderDate(LocalDateTime.now());
            newOrder.setPaymentStatus("Unpaid");
            newOrder.setShippingStatus("Pending");
            newOrder.setPaymentMethod(request.getPaymentMethod());
            newOrder.setExpectedDeliveryDate(LocalDateTime.now().plusDays(3));
            Order savedOrder = orderRepository.save(newOrder);

            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem item : entry.getValue()) {
                OrderItem detail = new OrderItem();
                detail.setOrder(savedOrder);
                detail.setProductVariant(item.getProductVariant());
                detail.setQuantity(item.getQuantity());
                detail.setPrice(item.getProductVariant() != null ? item.getProductVariant().getPrice() : null);
                orderItems.add(detail);
            }
            orderItemRepository.saveAll(orderItems);
            orderCount++;
        }

        // Xóa các sản phẩm đã đặt khỏi giỏ
        cartItemRepository.deleteAllById(request.getCartItemIds());

        // Lưu thông báo "đặt hàng thành công" vào chuông (không để lỗi thông báo phá đơn hàng)
        try {
            notificationService.create(
                    buyer.getUserID(),
                    "ORDER",
                    "Đặt hàng thành công",
                    orderCount > 1
                            ? "Bạn đã đặt thành công " + orderCount + " đơn hàng. Cảm ơn bạn đã mua sắm tại ShopZone!"
                            : "Đơn hàng của bạn đã được đặt thành công. Cảm ơn bạn đã mua sắm tại ShopZone!",
                    "/account/order"
            );
        } catch (Exception e) {
            System.err.println("Không tạo được thông báo đặt hàng: " + e.getMessage());
        }

        return orderCount > 1
                ? "Đặt hàng thành công! Đã tạo " + orderCount + " đơn theo từng shop."
                : "Đặt hàng thành công!";
    }

    public List<Order> getOrderHistory(Long userId) {
        return orderRepository.findByBuyer_UserIDOrderByOrderDateDesc(userId);
    }

    // Lịch sử đơn hàng kèm danh sách sản phẩm trong mỗi đơn (cho trang Account)
    public List<OrderHistoryDTO> getOrderHistoryDTO(Long userId) {
        List<Order> orders = orderRepository.findByBuyer_UserIDOrderByOrderDateDesc(userId);
        List<OrderHistoryDTO> result = new ArrayList<>();

        for (Order order : orders) {
            OrderHistoryDTO dto = new OrderHistoryDTO();
            dto.setOrderID(order.getOrderID());
            dto.setOrderDate(order.getOrderDate());
            dto.setShippingStatus(order.getShippingStatus());
            dto.setPaymentStatus(order.getPaymentStatus());
            dto.setPaymentMethod(order.getPaymentMethod());
            if (order.getShop() != null) {
                dto.setShopID(order.getShop().getShopID());
                dto.setShopName(order.getShop().getShopName());
            }

            List<OrderItem> items = orderItemRepository.findByOrder_OrderID(order.getOrderID());
            List<OrderHistoryDTO.Item> dtoItems = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (OrderItem oi : items) {
                OrderHistoryDTO.Item item = new OrderHistoryDTO.Item();
                item.setOrderItemID(oi.getOrderItemID());
                item.setQuantity(oi.getQuantity());
                item.setPrice(oi.getPrice());

                ProductVariant variant = oi.getProductVariant();
                if (variant != null) {
                    item.setVariantID(variant.getVariantID());
                    item.setSize(variant.getSize());
                    item.setColor(variant.getColor());
                    if (variant.getProduct() != null) {
                        Long pid = variant.getProduct().getProductID();
                        item.setProductID(pid);
                        item.setProductName(variant.getProduct().getProductName());
                        // ảnh chính của sản phẩm (nếu có)
                        List<ProductImage> imgs = productImageRepository.findByProduct_ProductIDOrderByIsMainDesc(pid);
                        if (!imgs.isEmpty()) item.setImageURL(imgs.get(0).getImageURL());
                    }
                }

                // Đã đánh giá orderItem này chưa + lấy nội dung đánh giá thật
                if (oi.getOrderItemID() != null) {
                    Review rv = reviewRepository.findByOrderItem(oi).orElse(null);
                    if (rv != null) {
                        item.setReviewed(true);
                        item.setRating(rv.getRating());
                        item.setComment(rv.getComment());
                        if (rv.getReviewDate() != null) {
                            item.setReviewDate(rv.getReviewDate()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        }
                    } else {
                        item.setReviewed(false);
                    }
                }

                if (oi.getPrice() != null && oi.getQuantity() != null) {
                    total = total.add(oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
                }
                dtoItems.add(item);
            }

            dto.setItems(dtoItems);
            dto.setTotalAmount(total);
            result.add(dto);
        }
        return result;
    }

    public String markOrderAsReceived(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        order.setShippingStatus("Confirmed");

        orderRepository.save(order);

        // Thông báo đã nhận hàng
        try {
            if (order.getBuyer() != null) {
                notificationService.create(
                        order.getBuyer().getUserID(),
                        "ORDER",
                        "Đã nhận hàng",
                        "Bạn đã xác nhận nhận hàng cho đơn #" + orderId + ". Đừng quên đánh giá sản phẩm nhé!",
                        "/account/order"
                );
            }
        } catch (Exception e) {
            System.err.println("Không tạo được thông báo nhận hàng: " + e.getMessage());
        }

        return "Xác nhận nhận hàng thành công!";
    }

    public OrderDetailModalDTO getOrderItemDetails(Long orderItemId) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng"));

        Order order = orderItem.getOrder();

        OrderDetailModalDTO dto = new OrderDetailModalDTO();
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setStatus(order.getShippingStatus());
        dto.setOrderDate(order.getOrderDate());

        Optional<Review> reviewOpt = reviewRepository.findByOrderItem(orderItem);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            dto.setReviewComment(review.getComment());
            dto.setReviewDate(review.getReviewDate());
            dto.setReviewRating(review.getRating());
        }
        return dto;
    }

    // Lấy danh sách hóa đơn của Shop (kèm các items trong mỗi đơn)
    public List<ShopOrderDTO> getShopOrders(Long shopId) {
        List<Order> orders = orderRepository.findByShop_ShopIDOrderByOrderDateDesc(shopId);
        List<ShopOrderDTO> result = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrder_OrderID(order.getOrderID());
            result.add(new ShopOrderDTO(order, items));
        }
        return result;
    }
}