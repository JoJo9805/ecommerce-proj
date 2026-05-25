import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/public/Order.css';
import bia from './assets/muado.jpg'; 
import cod from './assets/cod.png'; 
import bank from './assets/bank-card.png'; 
import momo from './assets/momo.png'; 

export default function Order({ onClose, subtotal, shippingFee, selectedItems }) { 
    const [vouchers, setVouchers] = useState({
        shipping: [
            { id: 'ship1', name: "Freeship Xtra - Giảm 15k", value: 15000 },
            { id: 'ship2', name: "Giảm 10k phí vận chuyển", value: 10000 }
        ],
        platform: [
            { id: 'p1', name: "ShopZone - Giảm 10k đơn từ 50k", value: 10000 },
            { id: 'p2', name: "ShopZone - Giảm 20k đơn từ 100k", value: 20000 }
        ],
        shops: {}
    });

    const cartItems = selectedItems;

    const [estimatedDate] = useState("15/03 - 20/03");

    const loggedUser = JSON.parse(localStorage.getItem('user'));
    const [userInfo, setUserInfo] = useState({
        phone: loggedUser?.phone || "",
        address: ""
    });

    // Nhập thông tin nhận hàng 
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setUserInfo(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const uniqueShops = [...new Set(cartItems.map(item => item.shop))];

    const [paymentMethod, setPaymentMethod] = useState('cod');

    //Quản lý voucher 
    const [selectedShipVoucher, setSelectedShipVoucher] = useState(0);
    const [selectedPlatformVoucher, setSelectedPlatformVoucher] = useState(0);
    const [selectedShopVouchers, setSelectedShopVouchers] = useState({});

    // Tính toán tiền
    const shopDiscountTotal = Object.values(selectedShopVouchers).reduce((a, b) => a + b, 0);
    const totalDiscount = selectedShipVoucher + selectedPlatformVoucher + shopDiscountTotal;
    const totalPayment = subtotal + shippingFee - totalDiscount;
 
    // Xác nhận đặt hàng (Đã nối API thật)
    const handlePlaceOrder = async () => {
        if (!userInfo.phone.trim() || !userInfo.address.trim()) {
            alert("⚠️ Vui lòng nhập đầy đủ Số điện thoại và Địa chỉ trước khi thanh toán!");
            return;
        }

        const user = JSON.parse(localStorage.getItem('user'));
        const userId = user?.userID || user?.userid || user?.id;

        if (!userId) {
            alert("Vui lòng đăng nhập lại để đặt hàng!");
            return;
        }

        // Lấy danh sách ID giỏ hàng
        const cartItemIds = cartItems.map(item => item.cartItemId);

        // Đóng gói dữ liệu theo OrderRequestDTO của Backend.
        // Không fix cứng addressId/shopId nữa:
        //  - Gửi thẳng SĐT + địa chỉ người dùng nhập -> backend tạo địa chỉ mới
        //  - shopId để trống -> backend tự suy từ sản phẩm trong giỏ (và tách đơn theo shop)
        const orderRequest = {
            userId: userId,
            cartItemIds: cartItemIds,
            paymentMethod: paymentMethod.toUpperCase(), // COD, BANK, MOMO
            receiverName: user?.fullName || user?.email || "Khách hàng",
            receiverPhone: userInfo.phone,
            receiverAddress: userInfo.address
        };

        try {
            const response = await fetch(`http://localhost:8081/api/orders/place`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem('token')}` // Kẹp thẻ thông hành
                },
                body: JSON.stringify(orderRequest)
            });

            if (response.ok) {
                onClose(); 
                window.location.reload(); 
            } else {
                const errorData = await response.text();
                alert("❌ Lỗi khi đặt hàng: " + errorData);
            }
        } catch (error) {
            console.error("Lỗi:", error);
            alert("Không thể kết nối đến máy chủ thanh toán!");
        }
    };

    useEffect(() => {
        const fetchSavedVouchers = async () => {
            const user = JSON.parse(localStorage.getItem('user'));

            if (!user) return;

            const userId = user.userID || user.userid || user.id;

            try {
                const res = await fetch(
                    `http://localhost:8081/api/vouchers/user/${userId}`
                );

                if (res.ok) {
                    const data = await res.json();

                    const shops = {};

                    data.forEach(v => {

                        const voucherData = {
                            id: v.voucherID,
                            name: v.voucherName || v.code || "Voucher",
                            value: v.discountValue || 0
                        };

                        // Voucher vận chuyển
                        const shopName =
                            v.shop?.shopName || "Shop";

                        if (!shops[shopName]) {
                            shops[shopName] = [];
                        }

                        shops[shopName].push(voucherData);
                        
                    });

                    setVouchers(prev => ({
                        ...prev,
                        shops
                    }));
                }

            } catch (err) {
                console.error(err);
            }
        };

        fetchSavedVouchers();
    }, []);

    return (
        <div className="order-overlay">
            <div className="order-container">
                <div className="order-header-top">
                    <h2>THANH TOÁN ĐƠN HÀNG</h2>
                    <button className="close-btn" onClick={onClose}>&times;</button>
                </div>

                <div className='order-main'>
                    {/* --- PHẦN 1: SẢN PHẨM --- */}
                    <section className="checkout-section products-summary card">
                        <h3>Đơn hàng của bạn</h3>
                        <div className="product-list">
                            {cartItems.map(item => (
                                <div className="product-item-mini" key={item.id}>
                                    <img src={item.image || bia} alt={item.title || item.name} className="product-img-mini" />
                                    <div className="product-info-mini">
                                        <p className="p-name"><strong>{item.title || item.name}</strong></p>
                                        <p className="p-qty-price">SL: {item.quantity} x <span className="p-price">{Number(item.price).toLocaleString('vi-VN')}đ</span></p>
                                    </div>
                                </div>
                            ))}
                        </div>
                        <div className="summary-row subtotal">
                            <span>Tạm tính:</span>
                            <strong>{subtotal.toLocaleString('vi-VN')}đ</strong>
                        </div>
                    </section>

                    {/* --- PHẦN 2: THÔNG TIN --- */}
                    <section className="checkout-section user-payment-info card">
                        <h3>Thông tin nhận hàng</h3>
                        <div className="info-group">
                            <label>* Số điện thoại:</label>
                            <div className="info-value-box">
                                <input 
                                    type="text" 
                                    name="phone" 
                                    value={userInfo.phone} 
                                    onChange={handleInputChange}
                                    placeholder="Nhập số điện thoại..."
                                />
                            </div>
                        </div>
                        <div className="info-group">
                            <label>* Địa chỉ nhận hàng:</label>
                            <div className="info-value-box address-box">
                                <textarea 
                                    name="address" 
                                    rows="2" 
                                    value={userInfo.address} 
                                    onChange={handleInputChange}
                                    className="address-textarea"
                                    placeholder="Nhập địa chỉ chi tiết..."
                                ></textarea>
                            </div>
                        </div>
                        <div className="payment-method-section">
                            <h4>Phương thức thanh toán</h4>
                            <div className="method-grid">
                                <div 
                                    className={`payment-card ${paymentMethod === 'cod' ? 'active' : ''}`}
                                    onClick={() => setPaymentMethod('cod')}
                                >
                                    <div className="payment-icon">
                                         <img src={cod} alt="" />
                                    </div>
                                    <span>COD</span>
                                </div>

                                <div 
                                    className={`payment-card ${paymentMethod === 'bank' ? 'active' : ''}`}
                                    onClick={() => setPaymentMethod('bank')}
                                >
                                    <div className="payment-icon">
                                         <img src={bank} alt="" />
                                    </div>
                                    <span>Chuyển khoản ngân hàng</span>
                                </div>

                                <div 
                                    className={`payment-card ${paymentMethod === 'momo' ? 'active' : ''}`}
                                    onClick={() => setPaymentMethod('momo')}
                                >
                                    <div className="payment-icon">
                                         <img src={momo} alt="" />
                                    </div>
                                    <span>Ví MoMo</span>
                                </div>
                            </div>
                        </div>
                        <div className='delivery-date'>
                            <h4>Ngày nhận hàng</h4>
                            <p className='delivery-date-text'>* Đảm bảo nhận hàng vào ngày 5/3 - 14/3</p>
                        </div>
                    </section>

                    {/* --- PHẦN 3: VOUCHER VÀ TỔNG --- */}
                    <section className="checkout-section vouchers-total card">
                        <h3><i className="fas fa-ticket-alt icon-orange"></i>Ưu đãi và Tổng thanh toán</h3>

                        <div className="voucher-selection-area">
                            {/* Voucher Vận chuyển */}
                            <div className="voucher-group">
                                <label style={{color: "#26aa99"}}>Voucher Vận chuyển:</label>
                                <select onChange={(e) => setSelectedShipVoucher(Number(e.target.value))}>
                                    <option value="0">Chọn mã freeship...</option>
                                    {vouchers.shipping.map(v => <option key={v.id} value={v.value}>{v.name}</option>)}
                                </select>
                            </div>

                            {/* Voucher Sàn */}
                            <div className="voucher-group">
                                <label className="orange-text">Voucher ShopZone:</label>
                                <select onChange={(e) => setSelectedPlatformVoucher(Number(e.target.value))}>
                                    <option value="0">Chọn mã giảm giá sàn...</option>
                                    {vouchers.platform.map(v => <option key={v.id} value={v.value}>{v.name}</option>)}
                                </select>
                            </div>

                            {/* Voucher Shop */}
                            {uniqueShops.map(shopName => (
                                <div className="voucher-group" key={shopName}>
                                    <label className="orange-text">Voucher từ {shopName}:</label>
                                    <select onChange={(e) => {
                                        const val = Number(e.target.value);
                                        setSelectedShopVouchers(prev => ({...prev, [shopName]: val}));
                                    }}>
                                        <option value="0">Chọn mã giảm giá shop...</option>
                                        {vouchers.shops[shopName]?.map(v => (
                                            <option key={v.id} value={v.value}>
                                                {`Giảm ${Number(v.value).toLocaleString('vi-VN')}đ`}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            ))}
                        </div>

                        <div className="final-summary">
                            <div className="summary-row">
                                <span>Tiền hàng:</span>
                                <span>{subtotal.toLocaleString('vi-VN')}đ</span>
                            </div>
                            <div className="summary-row">
                                <span>Phí ship:</span>
                                <span>{shippingFee.toLocaleString('vi-VN')}đ</span>
                            </div>
                            <div className="summary-row">
                                <span>Giảm giá:</span>
                                <span>-{totalDiscount.toLocaleString('vi-VN')}đ</span>
                            </div>
                            <div className="summary-row total-row">
                                <strong>Tổng cộng:</strong>
                                <strong className="orange-text">{totalPayment.toLocaleString('vi-VN')}đ</strong>
                            </div>
                        </div>
                        <button className="btn-place-order-final" onClick={handlePlaceOrder}>
                            Xác nhận đặt hàng
                        </button>
                    </section>
                </div>
            </div>
        </div>
    );
}