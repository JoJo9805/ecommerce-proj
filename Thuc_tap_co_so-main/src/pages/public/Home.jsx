import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../../styles/public/Home.css';
import book from './assets/men.png'; 
import cosmetics from './assets/cosmetics.png';
import fashion from './assets/fashion.png';
import household from './assets/household.png';
import international from './assets/international.png';
import phone from './assets/phone.png';
import selling from './assets/selling.png';
import sport from './assets/sport.png';
import qc1 from './assets/quangcao1.png';
import qc2 from './assets/quangcao2.png';
import defaultImg from './assets/muado.jpg';

export default function Home() {
    const [products, setProducts] = useState([]);
    const [page, setPage] = useState(0);          
    const [hasMore, setHasMore] = useState(true);  
    const [loading, setLoading] = useState(false);
    
    // Quản lý tab danh mục giao diện đang được chọn (null tức là hiển thị tất cả)
    const [activeCollection, setActiveCollection] = useState(null);

    const navigate = useNavigate();
    const location = useLocation();
    const PAGE_SIZE = 40;

    const searchParams = new URLSearchParams(location.search);
    const searchTerm = searchParams.get('search');

    // Cấu hình 8 danh mục tương thích chuẩn xác 100% với 8 bảng UI trong CSDL
    const menuCollections = [
        { id: 'best-sellers', name: 'Sản phẩm bán chạy', icon: selling },
        { id: 'international', name: 'Săn hàng quốc tế', icon: international },
        { id: 'womens-fashion', name: 'Thời trang nữ', icon: fashion },
        { id: 'mens-fashion', name: 'Thời trang nam', icon: book }, 
        { id: 'beauty', name: 'Làm đẹp', icon: cosmetics },
        { id: 'electronics', name: 'Đồ điện tử', icon: phone },
        { id: 'sports', name: 'Thể thao', icon: sport },
        { id: 'home-appliances', name: 'Đồ gia dụng', icon: household }
    ];

    // Khi người dùng tìm kiếm từ thanh Search: Reset bộ lọc danh mục trái về mặc định
    useEffect(() => {
        setProducts([]);
        setPage(0);
        setHasMore(true);
        setActiveCollection(null); 
    }, [searchTerm]);

    // Khi người dùng click chọn danh mục bên trái
    const handleCollectionClick = (collectionId) => {
        if (activeCollection === collectionId) {
            setActiveCollection(null); 
        } else {
            setActiveCollection(collectionId);
        }
        setProducts([]);
        setPage(0);
        setHasMore(true);
        if (location.search) {
            navigate('/');
        }
    };

    useEffect(() => {
        const fetchProducts = async () => {
            setLoading(true);
            try {
                let apiUrl;
                if (searchTerm) {
                    apiUrl = `http://localhost:8081/api/products/search?keyword=${encodeURIComponent(searchTerm)}&page=${page}&size=${PAGE_SIZE}`;
                } else if (activeCollection) {
                    apiUrl = `http://localhost:8081/api/products/collection/${activeCollection}?page=${page}&size=${PAGE_SIZE}`;
                } else {
                    apiUrl = `http://localhost:8081/api/products?page=${page}&size=${PAGE_SIZE}`;
                }

                const response = await fetch(apiUrl);
                if (response.ok) {
                    const data = await response.json();
                    const newItems = data.content || [];

                    setProducts(prev => {
                        if (page === 0) return newItems;
                        const existingIds = new Set(prev.map(p => p.productID));
                        const merged = newItems.filter(p => !existingIds.has(p.productID));
                        return [...prev, ...merged];
                    });

                    setHasMore(!data.last && newItems.length > 0);
                } else {
                    setHasMore(false);
                }
            } catch (error) {
                console.error("Lỗi kết nối:", error);
                setHasMore(false);
            } finally {
                setLoading(false);
            }
        };
        fetchProducts();
    }, [searchTerm, activeCollection, page]);

    // IntersectionObserver tự động scroll tải trang kế tiếp
    const observerRef = useRef(null);
    const sentinelRef = useCallback((node) => {
        if (loading) return;
        if (observerRef.current) observerRef.current.disconnect();

        observerRef.current = new IntersectionObserver(entries => {
            if (entries[0].isIntersecting && hasMore && !loading) {
                setPage(prev => prev + 1);
            }
        });

        if (node) observerRef.current.observe(node);
    }, [loading, hasMore]);

    const handleAddToCart = async (e, variantId) => {
        e.stopPropagation();
        if (!variantId) {
            alert("Sản phẩm này chưa có phân loại nên chưa thể thêm vào giỏ.");
            return;
        }

        const user = JSON.parse(localStorage.getItem('user'));
        if (!user) {
            alert("Vui lòng đăng nhập để thêm đồ vào giỏ nhé!");
            navigate('/login');
            return;
        }

        const userId = user.userID || user.userid || user.id;

        try {
            const response = await fetch(`http://localhost:8081/api/carts/add`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify({ userId, variantId, quantity: 1 })
            });

            const message = await response.text();
            if (response.ok) {
                alert("🛒 " + message);
            } else {
                alert("Lỗi: " + (message || "Không thể thêm vào giỏ."));
            }
        } catch (error) {
            alert("Không thể kết nối đến server!");
        }
    };

    const renderStars = (score) => {
        const positiveStars = Math.round(score || 5); 
        return "⭐".repeat(positiveStars) + "☆".repeat(5 - positiveStars);
    };

    return (
        <div className='home'>
            {/* Thanh điều hướng bên trái được tạo động và có sự kiện Click */}
            <div className='home_navbar_left'>
                {menuCollections.map((col) => (
                    <div 
                        className={`home_options ${activeCollection === col.id ? 'active_menu_tab' : ''}`} 
                        key={col.id}
                        onClick={() => handleCollectionClick(col.id)}
                    >
                        <img className='home_options_img' src={col.icon} alt={col.name} />
                        <b style={{ color: activeCollection === col.id ? '#EE4D2D' : '#333' }}>{col.name}</b>
                    </div>
                ))}
            </div>

            {/* Nội dung chính giữa */}
            <div className='home_main_content'>
                <div className='home_ad'>
                    <img className='home_qc' src={qc1} alt="Ad 1" />
                    <img className='home_qc_2' src={qc2} alt="Ad 2" />
                </div>

                <div className='home_products'>
                    {products.length === 0 && !loading ? (
                        <p style={{ textAlign: "center", padding: "20px", width: "100%" }}>
                            Không tìm thấy sản phẩm nào phù hợp trong mục này.
                        </p>
                    ) : (
                        products.map((item) => {
                            const pid = item.productID;
                            const vid = item.variantID;

                            return (
                                <div
                                    className='product'
                                    key={pid}
                                    onClick={() => navigate(`/product/${pid}`)}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <img
                                        src={item.imageURL || defaultImg} 
                                        alt={item.productName || "Sản phẩm"}
                                        className="product_img"
                                    />
                                    <div className="product_name">
                                        {item.productName || "Sản phẩm chưa có tên"}
                                    </div>
                                    
                                    <div className="rating">
                                        {renderStars(item.rating)}
                                        <span className="product_sold">
                                            {item.soldCount > 0 ? ` Đã bán ${item.soldCount}` : ""}
                                        </span>
                                    </div>

                                    <div className="product_bottom">
                                        <span className="product_price">
                                            {item.price 
                                                ? Number(item.price).toLocaleString('vi-VN') + " ₫" 
                                                : "Liên hệ"}
                                        </span>
                                        <button 
                                            className="product_btn"
                                            disabled={!vid}
                                            title={!vid ? "Sản phẩm chưa có phân loại" : ""}
                                            onClick={(e) => handleAddToCart(e, vid)}
                                        >
                                            Add to Cart
                                        </button>
                                    </div>
                                </div>
                            );
                        })
                    )}
                </div>

                <div ref={sentinelRef} className="home_scroll_sentinel">
                    {loading && <span className="home_loading">Đang tải thêm sản phẩm...</span>}
                    {!hasMore && products.length > 0 && (
                        <span className="home_end">— Đã hết sản phẩm —</span>
                    )}
                </div>
            </div>
        </div>
    );
}