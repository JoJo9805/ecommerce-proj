import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "../../styles/public/Account.css"
import avt from './assets/avt-shop.jpg';

export default function Account() {
    const { active_tab } = useParams();
    const navigate = useNavigate(); 
    const activeTab = active_tab || "profile";

    const [isReviewModalVisible, setIsReviewModalVisible] = useState(false);
    const [reviewTarget, setReviewTarget] = useState(null);
    const [reviewForm, setReviewForm] = useState({ rating: 5, comment: "" });

    const [isModalVisible, setIsModalVisible] = useState(false);
    const [selectedItem, setSelectedItem] = useState(null); 
    const [isEditing, setIsEditing] = useState(false);

    // 1. STATE DỮ LIỆU USER
    const [userData, setUserData] = useState({
        username: "", fullName: "", email: "", phone: "", address: "Chưa cập nhật", password: "", birthday: "", gender: "Nữ", avatar: avt
    });

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        if (storedUser) {
            setUserData({
                username: storedUser.email ? storedUser.email.split('@')[0] : "user",
                fullName: storedUser.fullName || "",
                email: storedUser.email || "",
                phone: storedUser.phone || "",
                address: "Chưa cập nhật", 
                password: "", 
                birthday: storedUser.birthday ? storedUser.birthday.split('T')[0] : "", 
                gender: storedUser.gender,
                avatar: avt
            });
        } else {
            alert("Vui lòng đăng nhập để xem thông tin!");
            navigate('/login');
        }
    }, [navigate]);

    const [orderedHistory, setOrderedHistory] = useState([]);

    // 2. KÉO LỊCH SỬ MUA HÀNG (Đã sửa để lấy được productId và orderItemId dùng cho việc Đánh giá)
     useEffect(() => {
        const fetchOrderHistory = async () => {
            const storedUser = JSON.parse(localStorage.getItem('user'));
            if (!storedUser) return;
            const userId = storedUser.userID || storedUser.userid || storedUser.id;

            try {
                const response = await fetch(`http://localhost:8081/api/orders/user/${userId}`, {
                    headers: { "Authorization": `Bearer ${localStorage.getItem('token')}` }
                });
                if (response.ok) {
                    const data = await response.json();
                    
                    const formattedHistory = data.map(order => {
                        // Backend DTO mới trả về mảng "items" thay vì "orderItems"
                        const firstItem = order.items && order.items.length > 0 ? order.items[0] : null;

                        return {
                            key: order.orderID || order.orderid || order.id,
                            orderItemId: firstItem?.orderItemID || firstItem?.id, 
                            productId: firstItem?.productID, // productID thật để gửi đánh giá
                            image: firstItem?.imageURL, // ảnh thật của sản phẩm (null nếu chưa có)
                            status: order.shippingStatus === "Pending" ? "Đang giao" : 
                                   (order.shippingStatus === "Confirmed" || order.shippingStatus === "Completed" ? "Đã giao" : order.shippingStatus),
                            // Backend DTO mới đã có sẵn productName
                            name: firstItem ? firstItem.productName : `Đơn hàng từ ${order.shopName || 'Cửa hàng'}`, 
                            quantity: firstItem ? firstItem.quantity : 1, 
                            // Số tiền thật phải trả (định dạng VND); fallback chữ cũ nếu thiếu
                            price: order.totalAmount != null
                                ? Number(order.totalAmount).toLocaleString('vi-VN') + " ₫"
                                : (order.paymentMethod === "COD" ? "Thanh toán khi nhận" : "Đã thanh toán"), 
                            date: order.orderDate ? new Date(order.orderDate).toLocaleDateString('vi-VN') : "Chưa cập nhật",
                            // Trạng thái đánh giá THẬT từ backend (không reset khi reload)
                            review: firstItem?.reviewed ? (firstItem.comment || "Đã đánh giá") : "", 
                            rating: firstItem?.reviewed ? (firstItem.rating || 0) : 0, 
                            reviewDate: firstItem?.reviewDate || ""
                        };
                    });
                    setOrderedHistory(formattedHistory);
                }
            } catch (error) { console.error("Lỗi khi tải lịch sử:", error); }
        };

        if (activeTab === "history") fetchOrderHistory();
    }, [activeTab]);

    const handleTabChange = (tabName) => { navigate(`/account/${tabName}`); setIsEditing(false); };

    // 3. HÀM CẬP NHẬT PROFILE
    const handleProfileButtonClick = async () => {
        if (!isEditing) {
            setIsEditing(true);
        } else {
            const requiredFields = ["email", "phone", "fullName"];
            if (requiredFields.some(field => !userData[field] || userData[field].trim() === "")) {
                alert("Vui lòng điền đầy đủ Tên, Email và Số điện thoại!"); return; 
            }
            if (window.confirm("Bạn có chắc chắn muốn lưu thông tin này?")) {
                try {
                    const storedUser = JSON.parse(localStorage.getItem('user'));
                    const userId = storedUser.userID || storedUser.userid || storedUser.id;

                    const response = await fetch(`http://localhost:8081/api/users/${userId}/profile`, {
                        method: "PUT", 
                        headers: { 
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${localStorage.getItem('token')}`
                        },
                        body: JSON.stringify({
                            fullName: userData.fullName, email: userData.email, phone: userData.phone,
                            birthday: userData.birthday || null, gender: userData.gender, password: userData.password 
                        })
                    });

                    if (response.ok) {
                        alert("Cập nhật thông tin thành công!"); setIsEditing(false);
                        const updatedUser = { ...storedUser, ...userData };
                        delete updatedUser.password; 
                        localStorage.setItem('user', JSON.stringify(updatedUser));
                    } else { alert("Lỗi: Không thể cập nhật. Email hoặc SĐT có thể đã bị trùng!"); }
                } catch (error) { alert("Không thể kết nối đến Server!"); }
            }
        }
    };

    const handleInputChange = (e) => setUserData({ ...userData, [e.target.name]: e.target.value });

    // 4. XÁC NHẬN NHẬN HÀNG
    const handleConfirmReceived = async (e, key) => {
        e.stopPropagation(); 
        try {
            const response = await fetch(`http://localhost:8081/api/orders/${key}/receive`, { 
                method: "PUT",
                headers: { "Authorization": `Bearer ${localStorage.getItem('token')}` }
            });
            if (response.ok) {
                const updatedHistory = orderedHistory.map(item => item.key === key ? { ...item, status: "Đã giao" } : item);
                setOrderedHistory(updatedHistory);
            } else { alert("Có lỗi xảy ra khi cập nhật trạng thái!"); }
        } catch (error) { alert("Không thể kết nối với Server Spring Boot!"); }
    };

    const showDetail = (item) => { setSelectedItem(item); setIsModalVisible(true); };
    const closeModal = () => { setIsModalVisible(false); setSelectedItem(null); };

    const openReviewModal = (e, item) => {
        e.stopPropagation(); 
        setReviewForm({
            rating: 5,
            comment: ""
        });
        setReviewTarget(item);
        setIsReviewModalVisible(true);
    };

    // 5. GỬI ĐÁNH GIÁ THẬT XUỐNG DATABASE (Đã kết nối API)
    const submitReview = async () => {
        if (!reviewForm.comment.trim()) { alert("Vui lòng nhập lời nhận xét!"); return; }
        
        const storedUser = JSON.parse(localStorage.getItem('user'));
        const userId = storedUser?.userID || storedUser?.userid || storedUser?.id;

        if (!reviewTarget.productId || !reviewTarget.orderItemId) {
            alert("Đơn hàng này thiếu thông tin mã sản phẩm, không thể đánh giá!");
            return;
        }

        const reviewPayload = {
            productId: reviewTarget.productId,
            userId: userId,
            orderItemId: reviewTarget.orderItemId,
            rating: parseInt(reviewForm.rating),
            comment: reviewForm.comment
        };

        try {
            const response = await fetch(`http://localhost:8081/api/reviews/create`, {
                method: "POST",
                headers: { 
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(reviewPayload)
            });

            if (response.ok) {
                const today = new Date().toLocaleDateString("vi-VN");
                const updatedHistory = orderedHistory.map(item => 
                    (item.key === reviewTarget.key) ? { ...item, review: reviewForm.comment, rating: reviewForm.rating, reviewDate: today } : item
                );
                setOrderedHistory(updatedHistory);
                setIsReviewModalVisible(false);
                setReviewForm({ rating: 5, comment: "" });
            } else {
                alert("Bạn đã đánh giá sản phẩm này rồi hoặc có lỗi xảy ra!");
            }
        } catch (error) {
            console.error("Lỗi:", error);
            alert("Không kết nối được tới máy chủ để gửi đánh giá!");
        }
    };

    const getStatusStyles = (status) => {
        switch (status) {
            case "Đã giao": return { dot: "#218838", text: "#218838" }; 
            case "Đang giao": return { dot: "#f1c40f", text: "#b8860b" }; 
            default: return { dot: "#aaa", text: "#333" };
        }
    };

    // Đăng ký bán hàng
    const handleBecomeSeller = async () => {
        try {
            const storedUser = JSON.parse(localStorage.getItem("user"));

            if (!storedUser) {
                alert("Vui lòng đăng nhập!");
                return;
            }

            const userId =
                storedUser.userID ||
                storedUser.userid ||
                storedUser.id;

            const requestBody = {
                userID: userId, 
                shopID: userId,
                shopName: storedUser.fullName || storedUser.email,
                description: "Shop mới mở"
            };

            const response = await fetch(
                `http://localhost:8081/api/shops/register`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${localStorage.getItem("token")}`
                    },
                    body: JSON.stringify(requestBody)
                }
            );

            if (response.ok) {
                const shop = await response.json();
                alert("Đăng ký bán hàng thành công!");
                const updatedUser = {
                    ...storedUser,
                    role: {
                        ...storedUser.role,
                        roleID: 2 
                    },
                    shopID: userId 
                };

                localStorage.setItem("user", JSON.stringify(updatedUser));

                navigate(`/seller/profile/${userId}`);
            } else {
                const errorText = await response.text();
                alert(errorText || "Không thể đăng ký bán hàng!");
            }

        } catch (error) {
            console.error("Lỗi:", error);
            alert("Không thể kết nối đến server!");
        }
    };

    // Chuyển sang trang người bán
    const handleGoSellerPage = () => {
        const storedUser = JSON.parse(localStorage.getItem("user"));

        if (!storedUser) {
            alert("Vui lòng đăng nhập!");
            return;
        }

        const roleId =
            storedUser?.role?.roleID ||
            storedUser?.role?.roleid ||
            storedUser?.role?.id;

        if (Number(roleId) !== 2) {
            alert("Bạn chưa có tài khoản bán hàng. Vui lòng đăng ký!");
            return;
        }

        const userId = storedUser.userID || storedUser.userid || storedUser.id;
        navigate(`/seller/profile/${userId}`);
    };

    return (
        <>
        <div className="account-container">
            <div className="account-sidebar">
                <div className="user-profile-brief">
                    <img src={avt} alt="Avatar" />
                    <p>{userData.username}</p>
                </div>

                <button className={`nav-item ${activeTab === "profile" ? "active" : ""}`} onClick={() => handleTabChange("profile")}>
                    <i className="bx bx-user"></i>
                    <p>Thông tin cá nhân</p>
                </button>

                <button className={`nav-item ${activeTab === "history" ? "active" : ""}`} onClick={() => handleTabChange("history")}>
                    <i className="bx bx-history"></i>
                    <p>Lịch sử đặt hàng</p>
                </button>

                <button className={`nav-item ${activeTab === "store" ? "active" : ""}`} onClick={() => handleTabChange("store")}>
                    <i className="bx bx-store"></i>
                    <p>Đăng ký bán hàng</p>
                </button>

                <button
                    className="nav-item logout-btn"
                    onClick={() => {
                        localStorage.removeItem("token");
                        localStorage.removeItem("user");
                        alert("Đăng xuất thành công!");
                        navigate("/login");
                    }}
                >
                    <i className="bx bx-log-out"></i>
                    <p>Đăng xuất</p>
                </button>
            </div>

            <div className="account-main-content">
                <div className="tab-content-wrapper">
                    {/* PROFILE SECTION */}
                    <div className="profile-details" style={{ display: activeTab === "profile" ? "flex" : "none" }}>
                        <div className="info-row"><p className="info-label">Tên đăng nhập</p><p className="info-value">{userData.username}</p></div>
                        <div className="info-row"><p className="info-label">Họ và tên</p><input name="fullName" className={`info-value ${!isEditing ? "readonly-input" : "editing-input"}`} value={userData.fullName} readOnly={!isEditing} onChange={handleInputChange} /></div>
                        <div className="info-row"><p className="info-label">Email</p><input name="email" className={`info-value ${!isEditing ? "readonly-input" : "editing-input"}`} value={userData.email} readOnly={!isEditing} onChange={handleInputChange} /></div>
                        <div className="info-row"><p className="info-label">Số điện thoại</p><input name="phone" className={`info-value ${!isEditing ? "readonly-input" : "editing-input"}`} value={userData.phone} readOnly={!isEditing} onChange={handleInputChange} /></div>
                        <div className="info-row"><p className="info-label">Địa chỉ hiện tại</p><input name="address" className={`info-value ${!isEditing ? "readonly-input" : "editing-input"}`} value={userData.address} readOnly={!isEditing} onChange={handleInputChange} /></div>
                        
                        <div className="info-row">
                            <p className="info-label">Ngày sinh</p>
                            {isEditing ? <input type="date" name="birthday" className="info-value editing-input" value={userData.birthday} onChange={handleInputChange}/> : <p className="info-value">{userData.birthday || "Chưa cập nhật"}</p>}
                        </div>

                        <div className="info-row">
                            <p className="info-label">Giới tính</p>
                            <div className="info-value">
                                <label style={{ marginRight: "50px" }}><input type="radio" name="gender" value="Nam" checked={userData.gender === "Nam" || userData.gender === "Male"} onChange={handleInputChange} disabled={!isEditing}/> Nam</label>
                                <label><input type="radio" name="gender" value="Nữ" checked={userData.gender === "Nữ" || userData.gender === "Female"} onChange={handleInputChange} disabled={!isEditing}/> Nữ</label>
                            </div>
                        </div>
                        <div className="info-row"><p className="info-label">Mật khẩu mới</p><input name="password" placeholder="Bỏ trống nếu không muốn đổi..." className={`info-value ${!isEditing ? "readonly-input" : "editing-input"}`} type={isEditing ? "text" : "password"} value={userData.password} readOnly={!isEditing} onChange={handleInputChange} /></div>

                        <button className="changeProfile" onClick={handleProfileButtonClick}>{isEditing ? "Lưu thay đổi" : "Thay đổi"}</button>
                    </div>

                    {/* HISTORY SECTION */}
                    <div className="history-list" style={{ display: activeTab === "history" ? "flex" : "none" }}>
                        {orderedHistory.map((item) => {
                            const styles = getStatusStyles(item.status);
                            return (
                                <div className="history-card" key={item.key} onClick={() => showDetail(item)}>
                                    <div className="item-ordered-info">
                                        <img className="item-ordered-img" src={item.image || avt} alt="item" />
                                        <div className="text-details">
                                            <h4>{item.name}</h4>
                                            <p>SL x {item.quantity}</p>
                                        </div>
                                    </div>
                                    <div className="status-info">
                                        <p className="item-price">{item.price}</p>
                                        <div className="badge"><span className="badge-text" style={{ color: styles.text }}>{item.status}</span></div>
                                        {item.status === "Đang giao" ? (
                                            <button className="confirm-btn" onClick={(e) => handleConfirmReceived(e, item.key)}>Đã nhận hàng</button>
                                        ) : (
                                            item.status === "Đã giao" && !item.review && (
                                                <button className="review-btn" onClick={(e) => openReviewModal(e, item)}>Đánh giá</button>
                                            )
                                        )}
                                    </div>
                                </div>
                            );
                        })}
                    </div>

                    {/* STORE SECTION */}
                    <div className="seller-register-box" style={{ display: activeTab === "store" ? "flex" : "none" }}>
                        <h1>Đăng ký trở thành Người bán</h1>

                        <p className="welcome-seller">Mở shop ngay hôm nay để tiếp xây dựng thương hiệu riêng của bạn.</p>

                        <div className="benefits">
                            <p>🚀 Tiếp cận hàng ngàn khách hàng mỗi ngày</p>
                            <p>📦 Quản lý sản phẩm và đơn hàng dễ dàng</p>
                            <p>💰 Theo dõi doanh thu và thống kê trực quan</p>
                            <p>🔥 Tham gia các chương trình Flash Sale hấp dẫn</p>
                            <p>🎯 Tăng độ nhận diện thương hiệu cá nhân</p>
                        </div>

                        <p className="sug-seller">💡 Chỉ mất vài giây để kích hoạt tài khoản người bán. Sau khi đăng ký, bạn có thể đăng sản phẩm và quản lý shop ngay lập tức.</p>

                        <button
                            className="register-shop-btn"
                            onClick={handleBecomeSeller}
                        >
                            Đăng ký bán hàng
                        </button>

                        <p className="seller-login-text">
                            Đã có tài khoản bán hàng?{" "}
                            <span onClick={handleGoSellerPage}>
                                Chuyển sang trang bán hàng!
                            </span>
                        </p>
                    </div>
                </div>
            </div>
        </div>  

        {/* MODAL CHI TIẾT */}
        <div className="modal-overlay" style={{ display: isModalVisible ? "flex" : "none" }} onClick={(e) => e.target.className === 'modal-overlay' && closeModal()}>
            <div className="modal-box">
                <span className="modal-close-btn" onClick={closeModal}>&times;</span>
                <h2 className="modal-title">Chi tiết đơn hàng</h2>
                {selectedItem && (
                    <div className="modal-grid">
                        <div className="modal-col">
                            <div className="data-field field-centered">
                                <img className="item-ordered-img" src={selectedItem.image || avt} alt="item" />
                                <p>Tên sản phẩm: <strong>{selectedItem.name}</strong></p>
                            </div>
                            <div className="data-field">Số lượng: {selectedItem.quantity}</div>
                            <div className="data-field">Ngày đặt: {selectedItem.date}</div>
                        </div>
                        <div className="modal-col">
                            <div className="data-field">Trạng thái: <strong>{selectedItem.status}</strong></div>
                            <div className="data-field">Giá tiền: {selectedItem.price}</div>
                            <div className="data-field review-field">Đánh giá: {selectedItem.review || "Chưa có"} {selectedItem.rating > 0 && ` (${selectedItem.rating} ★)`}</div>
                            {selectedItem.reviewDate && <div className="data-field">Ngày đánh giá: {selectedItem.reviewDate}</div>}
                        </div>
                    </div>
                )}
            </div>
        </div>

        {/* MODAL VIẾT ĐÁNH GIÁ */}
        {isReviewModalVisible && (
            <div className="modal-overlay"  onClick={(e) => {if (e.target.className === "modal-overlay") {setIsReviewModalVisible(false);}}}>
                <div className="modal-box review-modal">
                    <h2 className="modal-title">Viết đánh giá ngay</h2>
                    <div className="rating-input">
                        <p>Chọn mức độ hài lòng:</p>
                        <select value={reviewForm.rating} onChange={(e) => setReviewForm({...reviewForm, rating: e.target.value})}>
                            <option value="5">5 ★ - Rất tốt</option>
                            <option value="4">4 ★ - Tốt</option>
                            <option value="3">3 ★ - Bình thường</option>
                            <option value="2">2 ★ - Tệ</option>
                            <option value="1">1 ★ - Rất tệ</option>
                        </select>
                    </div>
                    <textarea className="review-textarea" placeholder="Nhập lời nhận xét của bạn về sản phẩm..." value={reviewForm.comment} onChange={(e) => setReviewForm({...reviewForm, comment: e.target.value})}></textarea>
                    <div className="modal-actions">
                        <button className="cancel-btn" onClick={() => setIsReviewModalVisible(false)}>Hủy</button>
                        <button className="submit-btn" onClick={submitReview}>Gửi đánh giá</button>
                    </div>
                </div>
            </div>
        )}
        </>
    );
}