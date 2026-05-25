import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import "../../styles/seller/ShopProfile.css";
import avt from '../public/assets/avt-shop.jpg';
import productImg from '../public/assets/muado.jpg';

export default function ShopProfile() {
    const { id } = useParams();
    const [shopData, setShopData] = useState(null);

    // Hiển thị thông tin Shop
    useEffect(() => {
        const fetchShop = async () => {
            try {const response = await fetch(`http://localhost:8081/api/shops/${id}`);
                if (!response.ok) {
                    throw new Error("Load shop failed");
                }

                const data = await response.json();
                setShopData(data);
                setDescription(data.shop?.description || "");

            } catch (err) {
                console.error("Load shop failed", err);
            }
        };

        if (id) {fetchShop();}
    }, [id]);

    // Quản lý Mô tả 
    const [description, setDescription] = useState("");
    const [isEditingDesc, setIsEditingDesc] = useState(false);

    // Quản lý Voucher 
    const [vouchers, setVouchers] = useState([]);
    // Lấy danh sách voucher của Shop
    useEffect(() => {
        const fetchVouchers = async () => {
            try {
                const response = await fetch(`http://localhost:8081/api/vouchers/shop/${id}`);
                if (!response.ok) {
                    throw new Error("Load vouchers failed");
                }
                const data = await response.json();
                
                const formattedData = data.map(v => ({
                    id: v.voucherID,
                    discount: formatMoney(v.discountValue),
                    target: formatMoney(v.minOrderValue),
                    total: v.quantity ?? 0,
                    remaining: v.remainingQuantity ?? 0,
                    expiry: formatDate(v.endDate)
                }));
                
                setVouchers(formattedData);
            } catch (err) {
                console.error("Load vouchers failed", err);
            }
        };

        if (id) {
            fetchVouchers();
        }
    }, [id]);

    const [showAddVoucher, setShowAddVoucher] = useState(false);
    const [newVoucher, setNewVoucher] = useState({ discount: '', target: '', total: '', expiry: '' });
    const [error, setError] = useState("");

    // Quản lý Sản phẩm & Modal Chi tiết
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [replyingTo, setReplyingTo] = useState(null);
    const [replyText, setReplyText] = useState("");
    const [isEditingProduct, setIsEditingProduct] = useState(false);

    const [editProduct, setEditProduct] = useState({
        description: "",
        variants: []
    });

    // Hiển thị sản phẩm 
    const [productList, setProductList] = useState([]);

    const fetchProductsByShop = async () => {
        try {
            const response = await fetch(`http://localhost:8081/api/products/shop/${id}/all`);
            if (!response.ok) throw new Error("Load products failed");
            const data = await response.json();

            const formattedProducts = data.map(p => {
                return {
                    id: p.productID,
                    name: p.productName,
                    brand: p.brand || "",
                    description: p.description || "",
                    image:
                        p.imageURL ||
                        p.images?.find(img => img.isMain)?.imageURL ||
                        p.images?.[0]?.imageURL ||
                        productImg,
                    price:
                        p.price ||
                        p.variants?.[0]?.price ||
                        0,
                    size: [
                        ...new Set(
                            p.variants?.map(v => v.size).filter(Boolean) || []
                        )
                    ],
                    color: [
                        ...new Set(
                            p.variants?.map(v => v.color).filter(Boolean) || []
                        )
                    ],
                    sku:
                        p.variants?.[0]?.sku || "",
                    quantity:
                        p.stockQuantity ||
                        p.variants?.reduce(
                            (sum, v) => sum + (v.stockQuantity || 0),
                            0
                        ) || 0,
                    rating: p.rating || 0,
                    sold: p.sold || 0,
                    reviews: p.reviews || []
                };
            });

            setProductList(formattedProducts);
        } catch (err) {
            console.error("Lỗi khi lấy danh sách sản phẩm:", err);
        }
    };

    useEffect(() => {
        if (id) {
            fetchProductsByShop();
        }
    }, [id]);

    // Xem chi tiết sản phẩm
    const fetchProductDetail = async (productId) => {
        try {

            const response = await fetch(
                `http://localhost:8081/api/products/${productId}`
            );

            if (!response.ok) {
                throw new Error("Không thể xem chi tiết sản phẩm");
            }

            const data = await response.json();

            const product = data.product || {};
            const variants = data.variants || [];
            const reviews = data.reviews || [];

            const formattedProduct = {
                id: product.productID,
                name: product.productName,
                description: product.description || "Chưa có mô tả",
                image:
                    product.imageURL ||
                    product.images?.find(img => img.isMain)?.imageURL ||
                    product.images?.[0]?.imageURL ||
                    productImg,
                price:
                    variants[0]?.price || 0,
                size: [
                    ...new Set(
                        variants
                            .map(v => v.size)
                            .filter(Boolean)
                    )
                ],
                color: [
                    ...new Set(
                        variants
                            .map(v => v.color)
                            .filter(Boolean)
                    )
                ],
                quantity:
                    variants.reduce(
                        (sum, v) => sum + (v.stockQuantity || 0),
                        0
                    ),
                sku:
                    variants[0]?.sku || "Chưa có SKU",
                rating: product.rating || 0,
                sold: product.sold || 0,
                reviews: reviews.map(r => ({
                    id: r.reviewID,
                    user: r.user?.fullName || "Người dùng",
                    text: r.comment,
                    rating: r.rating,
                    reply: r.shopReply || "",
                    time: r.createdAt || ""
                }))
            };

            setSelectedProduct(formattedProduct);

            setEditProduct({
                productName: product.productName,
                description: product.description || "",
                brand: product.brand || "Mặc định",
                variants: variants.map(v => ({
                    variantID: v.variantID || v.productVariantID,
                    size: v.size || "",
                    color: v.color || "",
                    price: v.price || 0,
                    stockQuantity: v.stockQuantity || 0,
                    sku: v.sku || ""
                }))
            });

        } catch (err) {
            console.error("Load product detail failed:", err);
        }
    };

    // Thêm sản phẩm mới
    const [showAddProduct, setShowAddProduct] = useState(false);
    const emptyProduct = {
        productName: "", 
        description: "", 
        brand: "Mặc định",
        categoryId: "1", 
        quantity: "",
        sku: "",
        variants: [{ size: "", color: "", price: "", stockQuantity: "", sku: "", status: "active" }],
        images: [{ imageURL: "", isMain: true }]
    };

    const [newProduct, setNewProduct] = useState(emptyProduct);
    //
    // Quản lý biến thể
    const addVariant = () => setNewProduct(f => ({
        ...f, variants: [...f.variants, { size: "", color: "", price: "", stockQuantity: "", sku: "", status: "active" }]
    }));
    const removeVariant = (idx) => setNewProduct(f => ({
        ...f, variants: f.variants.filter((_, i) => i !== idx)
    }));
    const updateVariant = (idx, field, val) => setNewProduct(f => ({
        ...f, variants: f.variants.map((v, i) => i === idx ? { ...v, [field]: val } : v)
    }));

    // Quản lý ảnh sản phẩm (Nhập URL trực tiếp)
    const addImage = () => setNewProduct(f => ({
        ...f, images: [...f.images, { imageURL: "", isMain: false }]
    }));
    const removeImage = (idx) => setNewProduct(f => ({
        ...f, images: f.images.filter((_, i) => i !== idx)
    }));
    const updateImage = (idx, field, val) => setNewProduct(f => ({
        ...f, images: f.images.map((img, i) => i === idx ? { ...img, [field]: val } : img)
    }));
    const setMainImage = (idx) => setNewProduct(f => ({
        ...f, images: f.images.map((img, i) => ({ ...img, isMain: i === idx }))
    }));
    //
    const formatNumber = (num) => {
        return num >= 1000 ? (num / 1000).toFixed(1) + 'k' : num;
    };
    //
    const renderStars = (score) => {
        const positiveStars = Math.round(score); 
        return "⭐".repeat(positiveStars) + "☆".repeat(5 - positiveStars);
    };
    
    const handleAddProduct = async () => {
        if (!newProduct.productName.trim()) { alert("Vui lòng nhập tên sản phẩm!"); return; }
        if (newProduct.variants.some(v => !v.price)) { alert("Vui lòng nhập giá cho tất cả biến thể!"); return; }
        if (newProduct.images.some(img => !img.imageURL.trim())) { alert("Vui lòng nhập URL ảnh!"); return; }

        const token = localStorage.getItem("token");
        
        const bodyPayload = {
            productName: newProduct.productName,
            description: newProduct.description,
            brand: newProduct.brand || "Mặc định",
            categoryId: newProduct.categoryId ? Number(newProduct.categoryId) : 1,
            shopId: Number(id),
            sku: newProduct.sku,
            quantity: parseInt(newProduct.quantity) || 0,
            variants: newProduct.variants.map(v => ({
                size: v.size, 
                color: v.color,
                price: parseFloat(v.price) || 0,
                stockQuantity: parseInt(v.stockQuantity) || 0,
                sku: v.sku || null,
                status: v.status || "active"
            })),
            images: newProduct.images.map(img => ({
                imageURL: img.imageURL,
                isMain: img.isMain
            }))
        };

        try {
            const response = await fetch("http://localhost:8081/api/products/seller", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(bodyPayload)
            });

            if (response.ok) {
                alert("Thêm sản phẩm mới thành công!");
                await fetchProductsByShop(); 
                setShowAddProduct(false);    
                setNewProduct(emptyProduct);  
            } else {
                const data = await response.json();
                alert(`❌ Lỗi: ${data.error || "Không thể lưu sản phẩm!"}`);
            }
        } catch (err) {
            console.error("Lỗi kết nối:", err);
            alert("Có lỗi xảy ra khi kết nối server!");
        }
    };

    // Chỉnh sửa sản phẩm
    const handleUpdateProduct = async () => {
        if (editProduct.variants.some(v => v.stockQuantity < 0 || v.stockQuantity === "")) { 
            alert("Số lượng kho không hợp lệ!"); 
            return; 
        }

        const token = localStorage.getItem("token");
        const bodyPayload = {
            productName: editProduct.productName,
            description: editProduct.description,
            brand: editProduct.brand,
            variants: editProduct.variants.map(v => ({
                variantID: v.variantID,
                size: v.size,
                color: v.color,
                price: parseFloat(v.price),
                stockQuantity: parseInt(v.stockQuantity) || 0, 
                sku: v.sku || null,
                status: "active"
            }))
        };

        try {
            const response = await fetch(`http://localhost:8081/api/products/seller/${selectedProduct.id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(bodyPayload)
            });

            if (response.ok) {
                alert("Cập nhật số lượng kho thành công!");
                setIsEditingProduct(false);
                await fetchProductsByShop(); 
                await fetchProductDetail(selectedProduct.id);
            } else {
                const data = await response.json();
                alert(`❌ Lỗi: ${data.error || "Không thể cập nhật số lượng!"}`);
            }
        } catch (err) {
            console.error("Lỗi cập nhật sản phẩm:", err);
            alert("Có lỗi xảy ra khi kết nối server!");
        }
    };

    /* Thêm voucher */
    const formatMoney = (value) => {
        const num = Number(value);
        if (isNaN(num)) return value;
        
        return num.toLocaleString('vi-VN') + " ₫"; 
    };
    //
    const formatDate = (dateStr) => {
        const date = new Date(dateStr);
        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();
        return `${day}.${month}.${year}`;
    };

    // Thêm Voucher
    const handleAddVoucher = async () => {
        // 1. Đã xóa !newVoucher.target
        if (!newVoucher.discount || !newVoucher.total || !newVoucher.expiry) {
            setError("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // 2. Đã xóa kiểm tra newVoucher.target < 0
        if (Number(newVoucher.discount) <= 0 || Number(newVoucher.total) <= 0) {
            setError("Giá trị phải lớn hơn 0!");
            return;
        }

        setError("");

        try {
            const now = new Date();
            const pad = (num) => String(num).padStart(2, '0');
            const formattedStartDate = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
            const formattedEndDate = `${newVoucher.expiry}T23:59:59`;
            const randomCode = "VC" + String(Math.floor(1000 + Math.random() * 9000));

            const bodyPayload = {
                voucherType: "Shop",
                discountValue: Number(newVoucher.discount),
                // 3. Đã XÓA dòng minOrderValue ở đây để không gửi lên Backend nữa
                quantity: Number(newVoucher.total),
                startDate: formattedStartDate,
                endDate: formattedEndDate,
                status: "Active", 
                voucherCode: randomCode, 
                shop: {
                    shopID: Number(id) 
                }
            };

            console.log("Voucher payload chuẩn gửi đi:", bodyPayload);
            const token = localStorage.getItem("token");

            const response = await fetch(
                "http://localhost:8081/api/vouchers",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`
                    },
                    body: JSON.stringify(bodyPayload)
                }
            );

            if (!response.ok) {
                const errorText = await response.text();
                console.error("Lỗi từ Backend trả về:", errorText);
                throw new Error(errorText || "Thêm voucher thất bại!");
            }

            const createdVoucher = await response.json();
            const formattedNewVoucher = {
                id: createdVoucher.voucherID,
                discount: formatMoney(createdVoucher.discountValue),
                // Cập nhật lại list không cần target nữa
                target: "", 
                total: createdVoucher.quantity ?? Number(newVoucher.total),
                remaining: createdVoucher.remainingQuantity ?? createdVoucher.quantity ?? Number(newVoucher.total),
                expiry: formatDate(createdVoucher.endDate)
            };

            setVouchers(prev => [formattedNewVoucher, ...prev]);
            setShowAddVoucher(false);
            setNewVoucher({ discount: "", target: "", total: "", expiry: "" });
            setError("");

        } catch (err) {
            console.error("Lỗi chi tiết khi gửi API:", err);
            setError(err.message || "Có lỗi xảy ra khi kết nối server!");
        }
    };
    //
    const handleImageUpload = (e) => {
        const files = Array.from(e.target.files);

        const imageUrls = files.map(file => URL.createObjectURL(file));

        setNewProduct({
            ...newProduct,
            images: [...newProduct.images, ...imageUrls]
        });
    };
    
    // Trả lời review 
    const handleReply = async (reviewId) => {
        if (!replyText.trim()) return;

        const token = localStorage.getItem("token");

        try {
            const response = await fetch(`http://localhost:8081/api/reviews/${reviewId}/reply`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}` 
                },
                body: JSON.stringify({
                    shopReply: replyText 
                })
            });

            if (response.ok) {
                const updatedReview = await response.json(); 
                alert("Trả lời đánh giá thành công!");

                setSelectedProduct(prevProduct => {
                    if (!prevProduct) return null;
                    return {
                        ...prevProduct,
                        reviews: prevProduct.reviews.map(rev => {
                            if (rev.id === reviewId) {
                                return {
                                    ...rev,
                                    reply: updatedReview.shopReply 
                                };
                            }
                            return rev;
                        })
                    };
                });

                setProductList(prevList => 
                    prevList.map(p => {
                        if (p.id === selectedProduct.id) {
                            return {
                                ...p,
                                reviews: (p.reviews || []).map(r => {
                                    const currentReviewId = r.reviewID || r.id;
                                    if (currentReviewId === reviewId) {
                                        return { 
                                            ...r, 
                                            shopReply: updatedReview.shopReply,
                                            reply: updatedReview.shopReply 
                                        };
                                    }
                                    return r;
                                })
                            };
                        }
                        return p;
                    })
                );
                setReplyText("");
                setReplyingTo(null);

            } else {
                const errorText = await response.text();
                alert(`❌ Lỗi từ server: ${errorText || "Không thể gửi phản hồi!"}`);
            }
        } catch (err) {
            console.error("Lỗi kết nối API trả lời comment:", err);
            alert("Có lỗi xảy ra khi kết nối tới server! Vui lòng kiểm tra lại mạng hoặc log Backend.");
        }
    };

    return (
        <div className='shop-container profile-mode'>
            {/* Thông tin shop */ }
            <div className='shop-header profile-mode'>
                <div className='shop-info'>
                    <div className='info-left'>
                        <img src={avt} alt="avatar" className='shop-avatar' />
                        <div className='shop-details'>
                            <h1>{shopData?.shop?.shopName}</h1>
                            <div className='rating'>
                                {renderStars(shopData?.shop?.rating || 0)}
                            </div>
                            {isEditingDesc ? (
                                <div className="edit-desc-area">
                                    <textarea 
                                        className="edit-textarea"
                                        value={description} 
                                        onChange={(e) => setDescription(e.target.value)} 
                                        autoFocus
                                        rows={3}
                                    />
                                    <br />
                                    <button className="btn-save-desc" onClick={() => setIsEditingDesc(false)}>Lưu</button>
                                </div>
                            ) : (
                                <p className='description' title="Bấm để chỉnh sửa" onClick={() => setIsEditingDesc(true)}>
                                    {description} <span className="edit-icon">✎</span>
                                </p>
                            )}
                        </div>
                    </div>

                    <div className='info-right'>
                        <div className='stats'>
                            <div className='stat-item'>
                                <strong>{shopData?.productCount || 0}</strong>
                                <span>Sản phẩm</span>
                            </div>
                            <div className='stat-item'>
                                <strong>{formatNumber(shopData?.shop?.user?.followerCount || 0)}</strong>
                                <span>Theo dõi</span>
                            </div>
                            <div className='stat-item'>
                                <strong>{formatNumber(shopData?.salesCount) || 0}</strong>
                                <span>Lượt bán</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Voucher & Sản phẩm */}
            <div className='shop-main-content profile-mode'>
                {/* Voucher */}
                <div className='shop-voucher'>
                    <h3>Voucher của Shop</h3>
                    <div className='voucher-list'>
                        <div className='add-voucher-btn' title="Thêm Voucher" onClick={() => setShowAddVoucher(true)}>
                            <span>+</span>
                        </div>

                        {vouchers.map((v) => (
                            <div key={v.id} className="voucher-card profile-v">
                                <div className='voucher-left'>
                                    <div className='voucher-content'>
                                        <p className='v-discount'>Giảm {v.discount}</p>
                                        <p className='v-target'>Cho đơn từ {v.target}</p>
                                        <p className='v-stats'>Số lượng: {v.total} | Còn lại: {v.remaining}</p>
                                        <p className='v-expiry'>HSD: {v.expiry}</p>
                                    </div>
                                    <div className='sawtooth'></div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Thêm voucher */}
                {showAddVoucher && (
                    <div 
                        className="modal-overlay"
                        onClick={() => setShowAddVoucher(false)}
                    >
                        <div 
                            className="voucher-form"
                            onClick={(e) => e.stopPropagation()}
                        >
                            <h4>Thêm Voucher Mới</h4>
                            <input 
                                type="text" 
                                placeholder="Giá trị voucher" 
                                value={newVoucher.discount}
                                onChange={e => {
                                    setNewVoucher({...newVoucher, discount: e.target.value});
                                    setError("");
                                }} 
                            />
                            <input 
                                type="text" 
                                placeholder="Số lượng" 
                                value={newVoucher.total}
                                onChange={e => {
                                    setNewVoucher({...newVoucher, total: e.target.value});
                                    setError("");
                                }} 
                            />
                            <input 
                                type="date" 
                                placeholder="Hạn sử dụng" 
                                value={newVoucher.expiry}
                                onChange={e => {
                                    setNewVoucher({...newVoucher, expiry: e.target.value});
                                    setError("");
                                }} 
                            />
                            {error && <p className="error-text">{error}</p>}
                            <div className="form-btns">
                                <button onClick={() => setShowAddVoucher(false)}>Hủy</button>
                                <button className='add-voucher' onClick={handleAddVoucher}>Thêm</button>
                            </div>
                        </div>
                    </div>
                )}

                {/* Sản phẩm */}
                <div className='shop-products'>
                    <h3>Sản phẩm</h3>
                    <div className='shop-item'>
                        <div 
                            className='add-product-btn'
                            title="Thêm sản phẩm"
                            onClick={() => setShowAddProduct(true)}
                        >
                            <span>+</span>
                        </div>
                        {productList.map((item) => (
                            <div className='each-product no-hover' key={item.id}>
                                <img src={item.image} alt={item.name} className="shop-product-img" />
                                <div className="shop-product-name">{item.name}</div>
                                <div className="rating">
                                    {renderStars(item.rating)} 
                                    <span className="product_sold">Đã bán {item.sold || 0}</span>
                                </div>
                                <div className="shop-product-bottom">
                                    <span className="shop-product-price">{Number(item.price).toLocaleString("vi-VN")} ₫</span>
                                    <button
                                        className="btn-view-detail"
                                        onClick={() => {
                                            setIsEditingProduct(false);
                                            fetchProductDetail(item.id);
                                        }}
                                    >
                                        Xem chi tiết
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>


            {/* Thêm sản phẩm */}
            {showAddProduct && (
                <div className="modal-overlay" onClick={() => setShowAddProduct(false)}>
                    <div className="product-form" onClick={(e) => e.stopPropagation()}>
                        <h3>Thêm sản phẩm mới</h3>

                        {newProduct.images.map((img, idx) => (
                            <div key={idx} className="image-row-item">
                                <input placeholder="URL ảnh" value={img.imageURL} onChange={e => updateImage(idx, "imageURL", e.target.value)} />
                                <label>
                                    <span> <input type="radio" name="mainImage" checked={img.isMain} onChange={() => setMainImage(idx)} /> Ảnh chính </span>
                                </label>
                                {newProduct.images.length > 1 && <button onClick={() => removeImage(idx)}>✕</button>}
                            </div>
                        ))}
                        <button className='add-image-item'onClick={addImage}>+ Thêm link ảnh</button>

                        <input 
                            type="text" placeholder="Tên sản phẩm" value={newProduct.productName} 
                            onChange={e => setNewProduct({...newProduct, productName: e.target.value})} 
                        />

                        <input 
                            type="text" placeholder="Thương hiệu" value={newProduct.brand} 
                            onChange={e => setNewProduct({...newProduct, brand: e.target.value})} 
                        />
                        <textarea
                            placeholder="Mô tả sản phẩm" value={newProduct.description}
                            onChange={e => setNewProduct({...newProduct, description: e.target.value})}
                        />

                        {newProduct.variants.map((v, idx) => (
                            <div key={idx} className="variant-item">
                                <div className="variant-row">
                                    <input placeholder="Size" value={v.size} onChange={e => updateVariant(idx, "size", e.target.value)} /> 
                                    <input placeholder="Màu" value={v.color} onChange={e => updateVariant(idx, "color", e.target.value)} /> 
                                    <input type="number" placeholder="Số lượng" value={v.stockQuantity} onChange={e => updateVariant(idx, "stockQuantity", e.target.value)} />
                                </div>
                                <div className="variant-row">
                                    <input type="number" placeholder="Giá" value={v.price} onChange={e => updateVariant(idx, "price", e.target.value)} /> 
                                    <input type="text" placeholder="SKU" value={v.sku || ""} onChange={e => updateVariant(idx, "sku", e.target.value)}/>
                                </div>
                                {newProduct.variants.length > 1 && <button onClick={() => removeVariant(idx)}>✕</button>}
                            </div>
                        ))}
                        <button className='add-variant-item' type="button" onClick={addVariant}>+ Thêm biến thể</button>

                        <div className="form-btns">
                            <button onClick={() => setShowAddProduct(false)}>
                                Hủy
                            </button>

                            <button
                                className="add-voucher"
                                onClick={handleAddProduct}
                            >
                                Thêm sản phẩm
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal Chi tiết Sản phẩm */}
            {selectedProduct && (
                <div 
                    className="modal-overlay"
                    onClick={() => {
                        setSelectedProduct(null);
                        setIsEditingProduct(false);
                    }}
                >
                    <div 
                        className="detail-modal"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <button 
                            className="close-x" 
                            onClick={() => {
                                setSelectedProduct(null);
                                setIsEditingProduct(false);
                            }}
                        >
                            &times;
                        </button>

                        <div className="detail-main">
                            <img src={selectedProduct.image} alt="" />

                            <div className="info">
                                <h2>{selectedProduct.name}</h2>
                                <div style={{ display: 'flex', flexDirection: 'row' }}>
                                    <span className='rating'>{renderStars(selectedProduct.rating)} </span> 
                                    <span>| Đã bán: {selectedProduct.sold}</span>
                                </div>
                                <span className="price-detail">
                                    {Number(selectedProduct.price).toLocaleString("vi-VN")} ₫
                                </span>
                                {isEditingProduct ? (
                                    <>
                                        <p className="product-desc"><strong>Mô tả sản phẩm: </strong>{selectedProduct.description}</p>
                                        {editProduct.variants.map((v, idx) => (
                                            <div key={v.variantID || idx} className="variant-edit-box" style={{background: '#f9f9f9', padding: '10px', borderRadius: '5px', marginBottom: '8px', display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
                                                <div style={{fontSize: '13px', color: '#333'}}>
                                                    <strong>Phân loại {idx + 1}:</strong> {v.color || "Mặc định"} - {v.size || "Mặc định"} 
                                                    <span style={{color: '#888', marginLeft: '10px'}}>({Number(v.price).toLocaleString("vi-VN")} ₫)</span>
                                                </div>
                                                <div style={{display: 'flex', alignItems: 'center', gap: '5px'}}>
                                                    <span style={{fontSize: '12px', color: '#555'}}>Số lượng:</span>
                                                    <input 
                                                        type="number" 
                                                        placeholder="Nhập số kho" 
                                                        value={v.stockQuantity} 
                                                        style={{width: '90px', padding: '4px 8px', textAlign: 'center', border: '1px solid #ccc', borderRadius: '4px'}}
                                                        onChange={e => {
                                                            const newVariants = [...editProduct.variants];
                                                            newVariants[idx].stockQuantity = e.target.value;
                                                            setEditProduct({...editProduct, variants: newVariants});
                                                        }} 
                                                    />
                                                </div>
                                            </div>
                                        ))}
                                    </>
                                ) : (
                                    <>
                                        <p className="product-desc"><strong>Mô tả sản phẩm: </strong>{selectedProduct.description}</p>

                                        <div className="meta">
                                            <p><strong>Size:</strong>{" "} {selectedProduct.size.join(", ")}</p>
                                            <p><strong>Màu:</strong>{" "} {selectedProduct.color.join(", ")}</p>
                                            <p><strong>Số Lượng:</strong>{" "} {selectedProduct.quantity}</p>
                                            <p><strong>SKU:</strong> {selectedProduct.sku}</p>
                                        </div>
                                    </>
                                )}

                                <button
                                    className="btn-edit-product"
                                    onClick={() => {
                                        if (isEditingProduct) {
                                            handleUpdateProduct(); 
                                        } else {
                                            setIsEditingProduct(true); 
                                        }
                                    }}
                                >
                                    {isEditingProduct ? "Lưu" : "Chỉnh sửa"}
                                </button>
                            </div>
                        </div>

                        {/* COMMENT */}
                        <div className="detail-rev">
                            <h4>Đánh giá sản phẩm</h4>

                            <div className='comment-list'>
                                {selectedProduct.reviews.map(comment => (
                                    <div key={comment.id} className='comment-item'>

                                        <div className='comment-user-info'>
                                            <strong>{comment.user}</strong>
                                            <span className='comment-stars'>{renderStars(comment.rating)}</span>
                                            <small className='comment-date'>{comment.time}</small>
                                        </div>

                                        <p className='comment-content'>{comment.text}</p>
                                        
                                        {!comment.reply && (
                                            <button 
                                                className="btn-reply"
                                                onClick={() => setReplyingTo(comment.id)}
                                            >
                                                Trả lời
                                            </button>
                                        )}

                                        {replyingTo === comment.id && (
                                            <div className="reply-box">
                                                <textarea 
                                                    placeholder="Nhập phản hồi..."
                                                    value={replyText}
                                                    onChange={(e) => setReplyText(e.target.value)}
                                                />

                                                <div className="reply-actions">
                                                    <button 
                                                        className="btn-send-reply"
                                                        onClick={() => handleReply(comment.id)}
                                                    >
                                                        Gửi
                                                    </button>
                                                </div>
                                            </div>
                                        )}

                                        {comment.reply && (
                                            <div className="shop-reply">
                                                <strong>{shopData?.shop?.shopName}</strong>
                                                <p>{comment.reply}</p>
                                            </div>
                                        )}

                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}