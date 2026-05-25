import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import '../../styles/public/Product.css';

// Import ảnh mặc định phòng trường hợp API chưa có ảnh
import defaultImg from './assets/muado.jpg';
import avt from './assets/avt-shop.jpg';

export default function Product() {
    const { id } = useParams();
    const navigate = useNavigate();

    // --- CÁC STATE LƯU DỮ LIỆU ---
    const [product, setProduct] = useState(null);
    const [shop, setShop] = useState(null);
    const [variants, setVariants] = useState([]);
    const [images, setImages] = useState([]);
    
    const [availableSizes, setAvailableSizes] = useState([]);
    const [availableColors, setAvailableColors] = useState([]);
    const [selectedSize, setSelectedSize] = useState('');
    const [selectedColor, setSelectedColor] = useState('');
    
    const [currentImage, setCurrentImage] = useState(defaultImg);
    const [currentPrice, setCurrentPrice] = useState(0);
    const [selectedVariantId, setSelectedVariantId] = useState(null);
    
    // Thêm State quản lý Số lượng mua
    const [quantity, setQuantity] = useState(1);
    
    // Thêm State quản lý Đánh giá sản phẩm
    const [reviews, setReviews] = useState([]);

    const [similarProducts, setSimilarProducts] = useState(null);

    // 1. GỌI API LẤY CHI TIẾT SẢN PHẨM VÀ ĐÁNH GIÁ
    useEffect(() => {
        const fetchProductDetail = async () => {
            try {
                // Kéo thông tin chi tiết sản phẩm
                const response = await fetch(`http://localhost:8081/api/products/${id}`);
                if (response.ok) {
                    const data = await response.json();
                    // Backend trả về ProductDetailDTO: { product, variants, images, reviews }
                    const prod = data.product || data;
                    setProduct(prod);
                    setShop(prod.shop || null);

                    const productImages = data.images || [];
                    if (productImages.length > 0) {
                        const imgUrls = productImages.map(img => img.imageURL || img.imageUrl);
                        setImages(imgUrls);
                        setCurrentImage(imgUrls[0]);
                    } else {
                        setImages([defaultImg]);
                        setCurrentImage(defaultImg);
                    }

                    const productVariants = data.variants || [];
                    setVariants(productVariants);

                    if (productVariants.length > 0) {
                        const sizes = [...new Set(productVariants.map(v => v.size).filter(Boolean))];
                        const colors = [...new Set(productVariants.map(v => v.color).filter(Boolean))];

                        setAvailableSizes(sizes);
                        setAvailableColors(colors);

                        if (sizes.length > 0) setSelectedSize(sizes[0]);
                        if (colors.length > 0) setSelectedColor(colors[0]);

                        setCurrentPrice(productVariants[0].price || 0);
                        setSelectedVariantId(productVariants[0].variantID);
                    }
                }

                // Kéo danh sách đánh giá của sản phẩm
                const reviewRes = await fetch(`http://localhost:8081/api/reviews/product/${id}`);
                if (reviewRes.ok) {
                    const reviewData = await reviewRes.json();
                    setReviews(reviewData);
                }
            } catch (error) {
                console.error("Lỗi khi tải chi tiết sản phẩm:", error);
            }
        };

        if (id) fetchProductDetail();
    }, [id]); // <-- KẾT THÚC USEEFFECT 1 Ở ĐÂY

    useEffect(() => {
        const timer = setTimeout(() => {
            window.scrollTo(0, 0);
            
            const productContainer = document.querySelector('.product-container');
            if (productContainer) {
                productContainer.scrollTo(0, 0);
            }
        }, 10); 

        return () => clearTimeout(timer);
    }, [id]);

    // 4. GỌI API AI KẾT HỢP SPRING BOOT ĐỂ LẤY DỮ LIỆU SỐNG CHUẨN XÁC
    useEffect(() => {
        const fetchSimilarProducts = async () => {
            try {
                
                // BƯỚC 1: Gọi Python
                const aiRes = await fetch(`http://localhost:8000/api/ai/pca-knn/similar/${id}`);
                
                // Nếu gọi AI thất bại, in ra lỗi luôn để dễ sửa
                if (!aiRes.ok) {
                    console.error("Lỗi khi gọi AI. Mã lỗi API:", aiRes.status);
                    setSimilarProducts([]); // Bỏ treo giao diện
                    return;
                }
                
                const aiData = await aiRes.json();
                
                // Dò tìm ID
                let recommendedIds = [];
                if (Array.isArray(aiData)) {
                    recommendedIds = aiData.map(item => item.productID || item.product_id || item.id || item);
                } else if (aiData.recommended_product_ids) {
                    recommendedIds = aiData.recommended_product_ids;
                } else if (aiData.data) {
                    recommendedIds = aiData.data.map(item => item.productID || item.product_id || item.id || item);
                }

                // FIX LỖI ÉP KIỂU: Ép toàn bộ ID về chuẩn Số Nguyên (Number)
                const numericRecommendedIds = recommendedIds.map(Number).filter(n => !isNaN(n));

                if (numericRecommendedIds.length === 0) {

                    setSimilarProducts([]);
                    return;
                }

                // BƯỚC 2: Gọi Spring Boot lấy ĐÍCH DANH từng sản phẩm theo ID của AI                
                // Tạo một loạt các yêu cầu gửi đi cùng lúc (Tốc độ bàn thờ)
                const productPromises = numericRecommendedIds.map(id => 
                    fetch(`http://localhost:8081/api/products/${id}`)
                        .then(res => res.ok ? res.json() : null)
                );
                
                // Đợi tất cả dữ liệu trả về
                const results = await Promise.all(productPromises);

                // BƯỚC 3: Lọc bỏ những API bị lỗi (null) và gò dữ liệu cho khớp với Giao diện Trang chủ
                const finalMatchedProducts = results
                    .filter(data => data !== null) // Xóa những ID thực sự không tồn tại
                    .map(data => {
                        // Spring Boot đang trả về ProductDetailDTO gồm { product, images, variants }
                        const p = data.product || data;
                        return {
                            ...p,
                            // Lấy ảnh đầu tiên làm ảnh đại diện
                            imageURL: (data.images && data.images.length > 0) 
                                ? (data.images[0].imageURL || data.images[0].imageUrl) 
                                : null,
                            // Lấy giá thấp nhất từ bảng phân loại
                            price: (data.variants && data.variants.length > 0) ? data.variants[0].price : 0,
                            // Lấy ID phân loại để bấm "Add to cart"
                            variantID: (data.variants && data.variants.length > 0) ? data.variants[0].variantID : null,
                        };
                    });
                
                if (finalMatchedProducts.length === 0) {
                    setSimilarProducts([]); // Nếu xui lắm 10 ID đều chết thì mới báo rỗng
                } else {
                    setSimilarProducts(finalMatchedProducts);
                }

            } catch (error) {
                console.error("Lỗi sập mạng hoặc không kết nối được Server:", error);
                setSimilarProducts([]);
            }
        };

        if (id) fetchSimilarProducts();
    }, [id]);

    // 2. TỰ ĐỘNG ĐỔI GIÁ VÀ VARIANT_ID KHI CHỌN SIZE/COLOR
    useEffect(() => {
        if (variants.length > 0) {
            const matchedVariant = variants.find(v =>
                v.size === selectedSize &&
                v.color === selectedColor
            );

            if (matchedVariant) {
                setCurrentPrice(matchedVariant.price || 0);
                setSelectedVariantId(matchedVariant.variantID);
            } else if (variants.length > 0) {
                setCurrentPrice(variants[0].price || 0);
                setSelectedVariantId(variants[0].variantID);
            }
        }
    }, [selectedSize, selectedColor, variants]);

    // Hàm xử lý tăng giảm số lượng
    const handleDecrease = () => {
        if (quantity > 1) setQuantity(prev => prev - 1);
    };

    const handleIncrease = () => {
        setQuantity(prev => prev + 1);
    };

    // Xử lý khi người dùng gõ vào ô input
    const handleQuantityChange = (e) => {
        const value = e.target.value;
        if (value === '') {
            setQuantity('');
            return;
        }

        const num = parseInt(value, 10);
        if (!isNaN(num) && num > 0) {
            setQuantity(num);
        }
    };

    const handleQuantityBlur = () => {
        if (quantity === '' || quantity < 1) {
            setQuantity(1);
        }
    };

    // 3. HÀM THÊM VÀO GIỎ HÀNG
    const handleAddToCart = async () => {
        const user = JSON.parse(localStorage.getItem('user'));
        const token = localStorage.getItem('token');
        if (!user || !token) {
            alert("Vui lòng đăng nhập để thêm đồ vào giỏ nhé!");
            navigate('/login');
            return;
        }
        if (!selectedVariantId) {
            alert("Vui lòng chọn Kích thước và Màu sắc hợp lệ!");
            return;
        }
        const userId = user.userID || user.userid || user.id;

        try {
            // GỬI CHUẨN ĐỊNH DẠNG JSON BODY XUỐNG BACKEND
            const response = await fetch(`http://localhost:8081/api/carts/add`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    userId: userId,
                    variantId: selectedVariantId,
                    quantity: quantity
                })
            });

            if (response.ok) {
                alert(`🛒 Đã thêm ${quantity} sản phẩm vào giỏ hàng!`);
            } else {
                alert("Lỗi: Không thể thêm vào giỏ.");
            }
        } catch (error) {
            alert("Không thể kết nối đến server!");
        }
    };

    const renderStars = (score) => {
        const positiveStars = Math.round(score || 5); 
        return "⭐".repeat(positiveStars) + "☆".repeat(5 - positiveStars);
    };
    const [visibleReviewsCount, setVisibleReviewsCount] = useState(3);
    const handleShowMoreReviews = () => {
        setVisibleReviewsCount (prevCount => prevCount + 5);
    };

    if (!product) return <div style={{marginTop: '120px', textAlign: 'center'}}>Đang tải dữ liệu sản phẩm...</div>;

    return (
        <div className='product-container'>
            {/* Chi tiết Sản phẩm */}
            <div className='item-details'>
                <div className='item-img-group'>
                    <img className='item-main-img' src={currentImage} alt={product.productName} />
                    <div className='item-sub-images'>
                        {images.map((img, index) => (
                            <img 
                                key={index} 
                                src={img} 
                                alt={`sub-${index}`} 
                                className={`sub-img ${currentImage === img ? 'active-thumb' : ''}`}
                                onClick={() => setCurrentImage(img)} 
                            />
                        ))}
                    </div>
                </div>
                <div className='item-details-group'>
                    <h1 className='product-name'>{product.productName}</h1>
                    
                    <div className='product-meta'>
                        {renderStars(product?.rating || 5)}
                        <span className='sales'> | Đã bán {product.soldCount || "0"}</span>
                    </div>

                    <div className='product-price'>{Number(currentPrice).toLocaleString('vi-VN')} ₫</div>

                    <div className='selection-group'>
                        <p>Vận chuyển:</p>
                        <p className='shipping-date-text'>* Đảm bảo nhận hàng vào ngày 5/5 - 14/5</p>
                    </div>
                    <br />

                    {/* Kích thước */}
                    {availableSizes.length > 0 && (
                        <div className='selection-group'>
                            <p>Kích thước:</p>
                            <div className='options'>
                                {availableSizes.map(size => (
                                    <button 
                                        key={size}
                                        className={`opt-btn ${selectedSize === size ? 'active' : ''}`}
                                        onClick={() => setSelectedSize(size)}
                                    >
                                        {size}
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}
                    <br />

                    {/* Màu sắc */}
                    {availableColors.length > 0 && (
                        <div className='selection-group'>
                            <p>Màu sắc: <strong>{selectedColor}</strong></p>
                            <div className='options'>
                                {availableColors.map(color => (
                                    <button 
                                        key={color}
                                        className={`opt-btn ${selectedColor === color ? 'active' : ''}`}
                                        onClick={() => setSelectedColor(color)}
                                    >
                                        {color}
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}
                    <br />

                    {/* Khối chọn Số lượng */}
                    <div className='selection-group'>
                        <p>Số lượng:</p>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginTop: '10px' }}>
                            <button 
                                onClick={handleDecrease}
                                style={{ padding: '4px 10px', border: '1px solid #ccc', background: 'white', cursor: 'pointer', borderRadius: '50%', fontSize: '16px'}}
                            >
                                -
                            </button>
                            <input 
                                type="text" 
                                inputMode="numeric"
                                pattern="[0-9]*"
                                value={quantity}
                                onChange={handleQuantityChange}
                                onBlur={handleQuantityBlur}
                                style={{ 
                                    width: '60px', padding: '8px 15px', textAlign: 'center', 
                                    border: '1px solid #ccc',  borderRadius: '8px', outline: 'none'
                                }}
                            />
                            <button 
                                onClick={handleIncrease}
                                style={{ padding: '4px 10px', border: '1px solid #ccc', background: 'white', cursor: 'pointer', borderRadius: '50%', fontSize: '16px'}}
                            >
                                +
                            </button>
                        </div>
                    </div>

                    <div className='add-to-cart'>
                        <button className='add-to-cart-btn' onClick={handleAddToCart}>Thêm vào giỏ hàng</button>
                    </div>
                </div>
            </div>

            <div className='product-description-bottom'>
                <div className='product-description-bottom-header'>
                    <h3>Mô tả sản phẩm</h3>
                </div>
                <p>{product.description || "Đang cập nhật mô tả sản phẩm..."}</p>
            </div>

            {/* Đánh giá */}
            <div className='comment-section'>
                <div className='comment-header'>
                    <h3>Đánh giá ({shop?.rating || 5}⭐)</h3>
                    {reviews.length > 0 && <span className='show-more-text' style={{ cursor: 'pointer' }} onClick={handleShowMoreReviews}>Hiển thị thêm</span>}
                </div>
                
                <div className='comment-list'>
                    {reviews.length === 0 ? (
                        <p style={{ padding: '10px 0', color: '#777', fontStyle: 'italic' }}>
                            Sản phẩm này chưa có đánh giá nào.
                        </p>
                    ) : (
                        reviews.slice(0, visibleReviewsCount).map((rev) => {
                            const revId = rev.reviewID || rev.reviewid || rev.id;
                            return (
                                <div key={revId} className='comment-item'>
                                    <div className='comment-user-info'>
                                        <strong>{rev.user?.fullName || rev.user?.username || "Khách hàng"}</strong>
                                        <span className='comment-stars'>{rev.rating}⭐</span>
                                        <small className='comment-date'>
                                            {rev.reviewDate ? new Date(rev.reviewDate).toLocaleDateString('vi-VN') : "Gần đây"}
                                        </small>
                                    </div>
                                    <p className='comment-content'>{rev.comment}</p>
                                </div>
                            );
                        })
                    )}
                </div>
            </div>

            {/* Shop Card */}        
            <div className='shop-section-container'>
                <div className='shop-section-profile'>
                    <Link to={`/shop/${shop?.shopID || 1}`} style={{ textDecoration: 'none' }}>
                        <img src={shop?.avatar || avt} alt="shop-avatar" className='shop-section-avatar' />
                    </Link>
                    <div className='shop-section-info'>
                        <Link to={`/shop/${shop?.shopID || 1}`} style={{ textDecoration: 'none' }}>
                            <h1 className='shop-section-name'>{shop?.shopName || "The Artisan Collective"}</h1>
                        </Link>
                        <div className='shop-section-rating-box'>
                            {renderStars(shop?.rating)}
                            <span className='shop-section-rating-text'>
                                {shop?.rating || 4.2} ({shop?.reviews || "1.2k"} Đánh giá)
                            </span>
                        </div>
                        <p className='shop-section-bio'>
                            {shop?.description || "Unique, ethically sourced handmade goods from global artisans. Since 2018."}
                        </p>
                    </div>
                </div>

                <div className='shop-section-metrics'>
                    <div className='shop-section-stats-grid'>
                        <div className='shop-section-stat-card'>
                            <strong className='shop-section-stat-value'>{product?.shopProductCount || 158}</strong>
                            <span className='shop-section-stat-label'>Sản phẩm</span>
                        </div>
                        <div className='shop-section-stat-card'>
                            <strong className='shop-section-stat-value'>
                                {shop?.followerCount >= 1000 
                                    ? (shop.followerCount / 1000).toFixed(1) + 'k' 
                                    : (shop?.followerCount || "8.2k")}
                            </strong>
                            <span className='shop-section-stat-label'>Theo dõi</span>
                        </div>
                        <div className='shop-section-stat-card'>
                            <strong className='shop-section-stat-value'>
                                {product?.shopTotalSales >= 1000 
                                    ? (product.shopTotalSales / 1000).toFixed(1) + 'k' 
                                    : (product?.shopTotalSales || "0")}
                            </strong>
                            <span className='shop-section-stat-label'>Lượt bán</span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Sản phẩm tương tự */}
            <div className='item-similar'>
                <h3>Sản phẩm tương tự bạn có thể thích</h3>
                
                {/* Dùng chung class "home_products" để kế thừa CSS dạng Grid */}
                <div className='home_products' style={{ padding: '0', width: '100%' }}>
                    {similarProducts === null ? (
                        <p style={{ color: '#888', width: '80vw'}}>Đang tìm kiếm gợi ý tốt nhất cho bạn...</p>
                    ) : similarProducts.length === 0 ? (
                        <p style={{ color: '#d9534f', fontWeight: 'bold', width: '80vw' }}>
                            Không tìm thấy sản phẩm gợi ý nào khớp ID! (Do Data AI và Data Spring Boot đang bị lệch)
                        </p>
                    ) : (
                        similarProducts.map((item) => {
                            const pid = item.productID;
                            const vid = item.variantID;

                            return (
                                <div
                                    className='product'
                                    key={pid}
                                    onClick={() => {
                                        navigate(`/product/${pid}`);
                                    }}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <img
                                        src={item.imageURL || defaultImg} 
                                        alt={item.productName || "Sản phẩm"}
                                        className="product_img"
                                    />
                                   
                                    <div className="shop-product-name">
                                        {item.productName || "Sản phẩm"}
                                    </div>
                                    
                                    <div className="rating">
                                        {renderStars(item.rating || 5)}
                                        <span className="product_sold">
                                            Đã bán {item.soldCount || 0}
                                        </span>
                                    </div>

                                    <div className="shop-product-bottom">
                                        <span className="shop-product-price">
                                            {item.price 
                                                ? Number(item.price).toLocaleString('vi-VN') + " ₫" 
                                                : "Liên hệ"}
                                        </span>
                                        <button 
                                            className="shop-product-btn"
                                            disabled={!vid}
                                            title={!vid ? "Chưa có phân loại" : ""}
                                            onClick={(e) => {
                                                e.stopPropagation(); 
                                                alert("Thêm vào giỏ: " + item.productName);
                                            }}
                                        >
                                            Add to Cart
                                        </button>
                                    </div>
                                </div>
                            );
                        })
                    )}
                </div>
            </div>
        </div>
    );
}