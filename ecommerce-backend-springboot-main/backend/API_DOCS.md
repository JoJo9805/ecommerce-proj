# Tài liệu API - Dự án Thương mại điện tử

Danh sách các endpoint dùng để kết nối Frontend và Backend.

---

# API Documentation

## 1. Đăng ký tài khoản
- **Method:** POST  
- **URL:** `/api/auth/register`

### Body (JSON)
```json
{
  "fullName": "Nguyễn Văn Test",
  "email": "test@gmail.com",
  "phone": "0987654321",
  "password": "123",
  "confirmPassword": "123",
  "birthday": "2005-01-01",
  "gender": "Nam"
}
```

---

## 2. Đăng nhập
- **Method:** POST  
- **URL:** `/api/auth/login`

### Body (JSON)
```json
{
  "emailOrPhone": "test@gmail.com",
  "password": "123"
}
```

---

## 3. Thêm mới danh mục
- **Method:** POST  
- **URL:** `/api/categories`

### Body (JSON)
```json
{
  "categoryName": "Điện thoại"
}
```

---

## 4. Thêm mới đơn vị vận chuyển
- **Method:** POST  
- **URL:** `/api/shipping-units`

### Body (JSON)
```json
{
  "name": "Giao Hàng Nhanh",
  "price": 30000
}
```

---

## 5. Thêm mới Role (Quyền)
- **Method:** POST  
- **URL:** `/api/roles`

### Body (JSON)
```json
{
  "roleName": "ADMIN"
}
```

---

## 6. Thêm mới Người dùng
- **Method:** POST  
- **URL:** `/api/users`

### Body (JSON)
```json
{
  "fullName": "Nguyen Van A",
  "email": "test_thanh_cong_123@gmail.com",
  "password": "123",
  "role": {
    "roleID": 1
  }
}
```

---

## 7. Thêm mới cửa hàng
- **Method:** POST  
- **URL:** `/api/shops`

### Body (JSON)
```json
{
  "shopName": "Cửa hàng Công nghệ",
  "user": {
    "userID": 1
  }
}
```

---

## 8. Thêm mới sản phẩm
- **Method:** POST  
- **URL:** `/api/products`

### Body (JSON)
```json
{
  "productName": "Laptop Gaming Pro 2026",
  "price": 25000000,
  "description": "Cấu hình mạnh mẽ cho lập trình viên",
  "category": {
    "categoryID": 1
  },
  "shop": {
    "shopID": 1
  }
}
```

---

## 9. Lấy danh sách tất cả sản phẩm
- **Method:** GET  
- **URL:** `/api/products`

---

## 10. Thêm sản phẩm vào giỏ
- **Method:** POST  
- **URL:** `/api/carts/add?userId=1&variantId=1`

---

## 11. Lấy danh sách & Tìm kiếm sản phẩm
- **Method:** GET  
- **URL:** `/api/products/search`

---

## 12. Lấy sản phẩm tương tự
- **Method:** GET  
- **URL:** `/api/products/5/similar?categoryId=1`

---

## 13. Lấy sản phẩm Trending hoặc Gợi ý cá nhân hóa
- **Method:** GET  
- **URL:** `/api/products/recommend?userId={userId}`

### Mô tả
- Nếu không truyền `userId` (hoặc tài khoản mới): Trả về danh sách sản phẩm **Trending**.  
- Nếu có `userId`: Trả về sản phẩm dựa trên lịch sử tìm kiếm và hành vi của người dùng.

---

## 14. Lấy sản phẩm theo Danh mục / Lựa chọn bên trái
- **Method:** GET  
- **URL:**  
  - `/api/products/search?categoryId={id}`  
  - `/api/products/filter?tag={tagName}`

### Mô tả
Lọc và hiển thị danh sách sản phẩm dựa trên danh mục được chọn từ menu bên trái hoặc theo các thẻ (**tags**).

---

## 15. Hiển thị đánh giá sản phẩm
- **Method:** GET  
- **URL:** `/api/products/{id}/reviews`

### Mô tả
Trả về danh sách tất cả đánh giá của khách hàng cho sản phẩm cụ thể (bao gồm số sao, bình luận và ngày đánh giá).

---

## 16. Hiển thị thông tin chi tiết Shop
- **Method:** GET  
- **URL:** `/api/shops/{id}`

### Mô tả
Trả về các thông tin cốt lõi của Shop (sử dụng `ShopProfileDTO`) bao gồm:
- Đối tượng `shop` (tên, mô tả, rating, `followerCount`)
- `productCount`: Tổng số lượng sản phẩm
- `salesCount`: Tổng số lượt bán

### Response (JSON)
```json
{
  "shop": {
    "shopID": 1,
    "shopName": "Cửa hàng Công nghệ",
    "followerCount": 8200,
    "rating": 4.5
  },
  "productCount": 158,
  "salesCount": 45000
}
```

---

## 17. Sản phẩm của Shop (Trang cá nhân & Sản phẩm khác)
- **Method:** GET  
- **URL:** `/api/products/shop/{shopId}?excludeId={id}`

### Mô tả
- Tham số `excludeId` là **không bắt buộc** (optional).
- Nếu không có `excludeId` (VD: `/api/products/shop/1`): Lấy **toàn bộ** sản phẩm của Shop (dùng cho trang cá nhân Shop).  
- Nếu có `excludeId` (VD: `/api/products/shop/1?excludeId=5`): Lấy sản phẩm cùng Shop nhưng loại trừ sản phẩm hiện tại (dùng cho mục **"Sản phẩm khác của Shop"**).

---

## 18. Lấy sản phẩm tương tự
- **Method:** GET  
- **URL:** `/api/products/{id}/similar`

### Mô tả
Trả về danh sách các sản phẩm có cùng danh mục hoặc cùng đặc tính với sản phẩm đang xem.

---

## 19. Cập nhật lượt theo dõi Shop (Follow / Unfollow)
- **Method:** POST  
- **URL:** `/api/shops/{id}/follow?isFollowing={true/false}`

### Params
- `isFollowing = true`: Tăng 1 lượt theo dõi  
- `isFollowing = false`: Giảm 1 lượt theo dõi  

---

## 20. Hiển thị Voucher của Shop
- **Method:** GET  
- **URL:** `/api/vouchers/shop/{shopId}`

### Mô tả
Lấy danh sách các mã giảm giá do Shop phát hành. 

### Response (JSON)
```json
[
  {
    "voucherID": 1,
    "voucherType": "Shop",
    "discountValue": 100000,
    "quantity": 100,
    "remainingQuantity": 50,
    "startDate": "2026-01-01T00:00:00",
    "endDate": "2026-12-31T23:59:59"
  }
]
```

---

## 21. Lưu Voucher vào ví người dùng (Sưu tầm)
- **Method:** POST  
- **URL:** `/api/vouchers/save?userId={userId}&voucherId={voucherId}`

### Mô tả
- Lưu mã voucher vào bảng `user_vouchers`  
- Chỉ những mã đã được lưu thành công vào ví mới có thể sử dụng khi thanh toán  
- Hệ thống tự động chặn lưu trùng  

---

## 22. Hiển thị thông báo người dùng
- **Method:** GET  
- **URL:** `/api/notifications/user/{userId}`

### Mô tả
Lấy danh sách thông báo (cập nhật đơn hàng, khuyến mãi, cảnh báo hệ thống), sắp xếp theo thời gian mới nhất lên đầu.

---

## 23. Hiển thị thông tin cá nhân người dùng
- Method: GET  
- URL: /api/users/{userId}/profile

### Response
{
  "userId": 1,
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@gmail.com",
  "phone": "0987654321",
  "gender": "Nam",
  "birthday": "2004-05-15"
}

---

## 24. Cập nhật thông tin người dùng
- Method: PUT  
- URL: /api/users/{userId}/profile

### Body
{
  "fullName": "Nguyễn Văn A (Updated)",
  "phone": "0999888777",
  "gender": "Nam",
  "birthday": "2004-05-15",
  "password": "newpassword123"
}

---

## 25. Thông tin thanh toán
- Method: GET  
- URL: /api/checkout/payment-methods

---

## 26. Thông tin vận chuyển
- Method: GET  
- URL: /api/checkout/estimate-delivery

---

## 27. Voucher vận chuyển
- Method: GET  
- URL: /api/vouchers/shipping

---

## 28. Voucher hệ thống
- Method: GET  
- URL: /api/vouchers/platform

---

## 29. Voucher theo shop
- Method: GET  
- URL: /api/vouchers/cart-shops

---

## 30. Đặt hàng
- Method: POST  
- URL: /api/orders/place

---

## 31. Xóa giỏ hàng sau đặt
- Method: DELETE  
- URL: /api/cart/{userId}/remove-ordered

---

## 32. Lịch sử đơn hàng
- Method: GET  
- URL: /api/orders/user/{userId}

---

## 33. Chi tiết đơn hàng
- Method: GET  
- URL: /api/orders/items/{orderItemId}/details

---

## 34. Xác nhận nhận hàng
- Method: PUT  
- URL: /api/orders/{orderId}/receive

---

## 35. Đánh giá sản phẩm
- Method: POST  
- URL: /api/reviews/create

---

## 36. Cập nhật mô tả của Shop
- **Method:** PUT  
- **URL:** `/api/shops/{id}/description`

### Mô tả
Cho phép chủ Shop cập nhật đoạn giới thiệu ngắn (mô tả) của cửa hàng.

### Body (JSON)
```json
{
  "description": "Unique, ethically sourced handmade goods from global artisans. Since 2018."
}
```

---

## 37. Thêm Voucher mới cho Shop
- **Method:** POST  
- **URL:** `/api/vouchers`

### Mô tả
Tạo một voucher giảm giá mới do Shop phát hành.

### Body (JSON)
```json
{
  "voucherType": "Shop",
  "discountValue": 50000,
  "minOrderValue": 150000,
  "quantity": 50,
  "startDate": "2026-05-15T00:00:00",
  "endDate": "2026-06-15T23:59:59",
  "shop": {
    "shopID": 1
  }
}
```

---

## 38. Thêm sản phẩm mới cho Shop
- **Method:** POST  
- **URL:** `/api/products`

### Mô tả
Tạo một sản phẩm mới thuộc về Shop của người bán.

### Body (JSON)
```json
{
  "productName": "The Java Handbook",
  "description": "Mô tả sản phẩm...",
  "brand": "NXB Kim Đồng",
  "category": { "categoryID": 1 },
  "shop": { "shopID": 1 }
}
```

---

## 39. Hiển thị thông tin chi tiết sản phẩm
- **Method:** GET  
- **URL:** `/api/products/{id}`

### Mô tả
Trả về thông tin đầy đủ của sản phẩm bao gồm:
- Thông tin cơ bản (`product`)
- Danh sách biến thể (`variants`): giá, size, màu, tồn kho...
- Danh sách bình luận (`reviews`): bao gồm cả phản hồi của shop (`shopReply`)

### Response (JSON)
```json
{
  "product": { "productID": 1, "productName": "The Java Handbook", ... },
  "variants": [
    { "variantID": 1, "price": 29.99, "size": "A4", "color": "Tắng đen", "stockQuantity": 50 }
  ],
  "reviews": [
    { "reviewID": 1, "rating": 4, "comment": "Sách hay!", "shopReply": "Cảm ơn bạn!" }
  ]
}
```

---

## 40. Cập nhật chỉnh sửa sản phẩm
- **Method:** PUT  
- **URL:** `/api/products/{id}`

### Mô tả
Cập nhật các thông tin của sản phẩm (tên, mô tả, thương hiệu, danh mục).

### Body (JSON)
```json
{
  "productName": "The Java Handbook (Updated Edition)",
  "description": "Mô tả mới...",
  "brand": "NXB Trẻ",
  "category": { "categoryID": 2 }
}
```

---

## 41. Cập nhật comment trả lời của Shop
- **Method:** PUT  
- **URL:** `/api/reviews/{reviewId}/reply`

### Mô tả
Cho phép chủ Shop trả lời công khai một bình luận của khách hàng. Nội dung phản hồi sẽ hiển thị ngay dưới comment gốc.

### Body (JSON)
```json
{
  "shopReply": "Cảm ơn bạn đã tin tưởng chúng tôi, rất vui khi bạn hài lòng!"
}
```

---

## 42. Hiển thị các hóa đơn của Shop
- **Method:** GET  
- **URL:** `/api/orders/shop/{shopId}`

### Mô tả
Trả về danh sách toàn bộ các đơn hàng khách đã đặt tại Shop, sắp xếp theo ngày mới nhất. Mỗi phần tử trả về bao gồm thông tin đơn hàng (`order`) và danh sách các sản phẩm trong đơn (`items`).

### Response (JSON)
```json
[
  {
    "order": {
      "orderID": 1,
      "buyer": { "fullName": "Nguyễn Văn A" },
      "orderDate": "2026-05-15T10:30:00",
      "shippingStatus": "Pending",
      "paymentStatus": "Unpaid"
    },
    "items": [
      { "orderItemID": 1, "quantity": 2, "price": 29.99 }
    ]
  }
]
```

---

## 43. Đăng ký bán hàng (Đăng ký Shop mới)
- **Method:** POST  
- **URL:** `/api/shops/register`

### Mô tả
Đăng ký người dùng hiện tại làm Người bán (Seller), tự động chuyển đổi Role của người dùng sang "Seller" (nếu chưa phải) và tạo bản ghi Cửa hàng (Shop) mới có ID tương ứng với ID người dùng.

### Request Body (JSON)
```json
{
  "userID": 40017,
  "shopName": "Cửa hàng của Tôi",
  "description": "Chuyên bán các sản phẩm chính hãng, giá tốt."
}
```

### Response (JSON)
```json
{
  "shopID": 40017,
  "shopName": "Cửa hàng của Tôi",
  "description": "Chuyên bán các sản phẩm chính hãng, giá tốt.",
  "rating": 0.0,
  "followerCount": 0,
  "createdAt": "2026-05-19T23:07:54",
  "updatedAt": "2026-05-19T23:07:54"
}
```
