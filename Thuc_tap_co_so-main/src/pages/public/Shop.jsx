import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../../styles/public/Shop.css';
import avt from './assets/avt-shop.jpg';
import defaultProductImg from './assets/muado.jpg';

export default function Shop() {
    const navigate = useNavigate();
    const { id } = useParams(); // Lấy ID shop từ URL thay vì name

    const [shopData, setShopData] = useState(null);
    const [products, setProducts] = useState([]);
    const [vouchers, setVouchers] = useState([]);
    const [isFollowed, setIsFollowed] = useState(false);
    const [followerCount, setFollowerCount] = useState(0);
    const [savedVouchers, setSavedVouchers] = useState([]);

    // 1. Fetch dữ liệu từ API khi Component Mount hoặc khi ID thay đổi
    useEffect(() => {
        const fetchShopData = async () => {
            try {
                const user = JSON.parse(localStorage.getItem('user'));
                
                // Lấy thông tin Shop (Hỗ trợ bóc tách nếu backend trả về dạng DTO lồng nhau)
                const shopRes = await fetch(`http://localhost:8081/api/shops/${id}`);
                if (shopRes.ok) {
                    const data = await shopRes.json();
                    const shop = data.shop || data;
                    setShopData(shop);
                    setFollowerCount(shop.followerCount || 0);
                }

                // Lấy danh sách Voucher của Shop
                const voucherRes = await fetch(`http://localhost:8081/api/vouchers/shop/${id}`);
                if (voucherRes.ok) {
                    setVouchers(await voucherRes.json());
                }

                // Lấy danh sách sản phẩm của Shop
                const productRes = await fetch(`http://localhost:8081/api/products/shop/${id}/all`);
                if (productRes.ok) {
                    setProducts(await productRes.json());
                }

                // Lấy voucher đã lưu
                if (user) {
                    const userId = user.userID || user.userid || user.id;

                    const savedRes = await fetch(
                        `http://localhost:8081/api/vouchers/saved/${userId}`
                    );

                    if (savedRes.ok) {
                        const savedIds = await savedRes.json();
                        setSavedVouchers(savedIds);
                    }

                    const followRes = await fetch(`http://localhost:8081/api/shops/${id}/check-follow?userId=${userId}`);
                    if (followRes.ok) {
                        const status = await followRes.json();
                        setIsFollowed(status);
                    }
                }
            } catch (error) {
                console.error("Lỗi khi tải trang Shop:", error);
            }
        };
        
        if (id) fetchShopData();
    }, [id]);

    // 2. Logic xử lý Theo dõi / Bỏ theo dõi Shop
    const handleFollow = async () => {
        const user = JSON.parse(localStorage.getItem('user'));
        if (!user) { 
            navigate('/login'); 
            return; 
        }
        const userId = user.userID || user.userid || user.id;
        const newStatus = !isFollowed;

        try {
            const res = await fetch(`http://localhost:8081/api/shops/${id}/follow?userId=${userId}&isFollowing=${newStatus}`, {
                method: "POST",
                headers: { "Authorization": `Bearer ${localStorage.getItem('token')}` }
            });
            if (res.ok) {
                const updatedShop = await res.json();
                setIsFollowed(newStatus);
                // Cập nhật số lượng follow trả về từ backend hoặc tự tăng/giảm ở client
                setFollowerCount(prev =>
                    updatedShop.followerCount !== undefined
                        ? updatedShop.followerCount
                        : (newStatus ? prev + 1 : prev - 1)
                );
            }
        } catch (error) { 
            console.error("Lỗi khi thực hiện follow:", error); 
        }
    };

    // 3. Logic xử lý Lưu mã giảm giá
    const handleSaveVoucher = async (voucherId) => {
        const user = JSON.parse(localStorage.getItem('user'));

        if (!user) { 
            navigate('/login'); 
            return; 
        }
        const userId = user.userID || user.userid || user.id;

        try {
            const res = await fetch(`http://localhost:8081/api/vouchers/save?userId=${userId}&voucherId=${voucherId}`, {
                method: "POST",
                headers: { "Authorization": `Bearer ${localStorage.getItem('token')}` }
            });
            if (res.ok) {
                if (!savedVouchers.includes(voucherId)) {
                    setSavedVouchers(prev => [...prev, voucherId]);
                }
            } else {
                alert("Không thể lưu mã, vui lòng thử lại!");
            }
        } catch (error) { 
            alert("Lỗi kết nối hệ thống!"); 
        }
    };

    // Format hiển thị số (Ví dụ: 8200 -> 8.2k)
    const formatNumber = (num) => {
        return num >= 1000 ? (num / 1000).toFixed(1) + 'k' : num;
    };

    // Render sao động dựa trên điểm số đánh giá
    const renderStars = (score) => {
        const positiveStars = Math.round(score || 5); 
        return "⭐".repeat(positiveStars) + "☆".repeat(5 - positiveStars);
    };

    // Giao diện loading khi chưa có dữ liệu Shop
    if (!shopData) {
        return <div style={{ marginTop: '120px', textAlign: 'center', fontSize: '18px' }}>Đang tải dữ liệu cửa hàng...</div>;
    }

    return (
        <div className='shop-container'>
            {/* THÔNG TIN HEADER SHOP */}
            <div className='shop-header'>
                <div className='shop-info'>
                    <div className='info-left'>
                        <img src={shopData.avatarURL || avt} alt="avatar" className='shop-avatar' />
                        <div className='shop-details'>
                            <h1>{shopData.shopName || shopData.name}</h1>
                            <div className='rating'>
                                {renderStars(shopData.rating)}
                                <span className='rating-score'>
                                    {shopData.rating || 5.0} ({shopData.reviewsCount || "0"} Đánh giá)
                                </span>
                            </div>
                            <p className='description'>{shopData.description || "Unique, ethically sourced handmade goods from global artisans. Since 2018."}</p>
                        </div>
                    </div>

                    <div className='info-right'>
                        <div className='action-buttons'>
                            <button 
                                className={`btn-follow ${isFollowed ? 'followed' : ''}`} 
                                onClick={handleFollow}
                            >
                                {isFollowed ? 'Đang theo dõi' : 'Theo dõi'}
                            </button>
                        </div>
                        <div className='stats'>
                            <div className='stat-item'>
                                <strong>{products.length}</strong>
                                <span>Sản phẩm</span>
                            </div>
                            <div className='stat-item'>
                                <strong>{formatNumber(followerCount)}</strong>
                                <span>Theo dõi</span>
                            </div>
                            <div className='stat-item'>
                                <strong>{shopData.salesCount || shopData.sales || "0"}</strong>
                                <span>Lượt bán</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* NỘI DUNG CHÍNH: VOUCHER & SẢN PHẨM */}
            <div className='shop-main-content'>
                {/* DANH SÁCH VOUCHER */}
                <div className='shop-voucher'>
                    <h3>Voucher của Shop</h3>
                    <div className='voucher-list'>
                        {vouchers.map((v) => {
                            const vId = v.voucherID || v.voucherid || v.id;
                            const isSaved = savedVouchers.includes(vId);
                            // Chỉ coi là hết khi remainingQuantity LÀ SỐ và <= 0.
                            // (null/undefined = chưa giới hạn số lượng -> vẫn cho lưu)
                            const isExpired = v.status === 'Hết'
                                || (typeof v.remainingQuantity === 'number' && v.remainingQuantity <= 0);

                            return (
                                <div key={vId} className={`voucher-card ${isExpired ? 'disabled' : ''}`}>
                                    <div className='voucher-left'>
                                        <div className='voucher-content'>
                                            <p className='v-discount'>Giảm {v.discountValue ? Number(v.discountValue).toLocaleString('vi-VN') + 'đ' : v.discount}</p>
                                            <p className='v-target'><span>{v.voucherType || v.target}</span></p>
                                            {v.expiryDate && <p className='v-expiry'>HSD: {new Date(v.expiryDate).toLocaleDateString('vi-VN')}</p>}
                                        </div>
                                        <div className='sawtooth'></div>
                                    </div>
                                    
                                    <div className='voucher-right'>
                                        <button 
                                            className={`btn-save ${isSaved ? 'saved' : ''}`}
                                            onClick={() => {
                                                if (isSaved) {
                                                    navigate('/my-vouchers');
                                                } else {
                                                    handleSaveVoucher(vId);
                                                }
                                            }}
                                            disabled={isExpired}
                                        >
                                            {v.status === 'Hết' ? 'Hết' : (isSaved ? 'Dùng ngay' : 'Lưu')}
                                        </button>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>

                {/* DANH SÁCH SẢN PHẨM */}
                <div className='shop-products'>
                    <h3>Tất cả sản phẩm</h3>
                    <div className='shop-item'>
                        {products.map((item) => {
                            const pId = item.productID || item.productid || item.id;
                            return (
                                <div 
                                    className='each-product' 
                                    key={pId}
                                    onClick={() => navigate(`/product/${pId}`)}
                                >
                                    <img
                                        src={item.imageURL || item.image || defaultProductImg}
                                        alt={item.productName || item.name}
                                        className="shop-product-img"
                                    />

                                    <div className="shop-product-name">
                                        {item.productName || item.name}
                                    </div>

                                    <div className="rating">
                                        {renderStars(item.rating || 5)} 
                                        <span className="product_sold">Đã bán {item.soldCount || item.sold || 0}</span>
                                    </div>

                                    <div className="shop-product-bottom">
                                        <span className="shop-product-price">
                                            {item.price ? Number(item.price).toLocaleString('vi-VN') + " ₫" : "Liên hệ"}
                                        </span>
                                        <button className="shop-product-btn">Add to Cart</button>
                                    </div>
                                </div>
                            );
                        })}
                    </div>

                    <p className='product-footer'>Không còn sản phẩm nào!</p>
                </div>
            </div>
        </div>
    );
}