import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/public/Login.css';
import bg from './assets/background-login.png';
import logo from './assets/sp-logo.png';
import shipping from './assets/shipping.png';
import voucher from './assets/voucher.png';
import payment from './assets/payment.png';

export default function Login() {
    const navigate = useNavigate();
    const [isRegisterOpen, setIsRegisterOpen] = useState(false);

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    // THÊM MỚI: State cho form Đăng ký
    const [regFullName, setRegFullName] = useState('');
    const [regEmail, setRegEmail] = useState('');
    const [regPhone, setRegPhone] = useState('');
    const [regPassword, setRegPassword] = useState('');
    const [regConfirmPassword, setRegConfirmPassword] = useState('');
    const [regBirthday, setRegBirthday] = useState('');
    const [regGender, setRegGender] = useState('');

    // THÊM MỚI: Hàm xử lý Đăng ký
    const handleRegister = async () => {
        // Kiểm tra cơ bản
        if (!regFullName || !regEmail || !regPassword || !regConfirmPassword) {
            alert("Vui lòng điền các thông tin bắt buộc!");
            return;
        }
        if (regPassword !== regConfirmPassword) {
            alert("Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            const response = await fetch("http://localhost:8081/api/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    fullName: regFullName,
                    email: regEmail,
                    phone: regPhone,
                    password: regPassword,
                    confirmPassword: regConfirmPassword,
                    birthday: regBirthday, // Chuẩn định dạng YYYY-MM-DD từ thẻ input type="date"
                    gender: regGender
                })
            });

            if (response.ok) {
                alert("Đăng ký thành công! Vui lòng đăng nhập.");
                // Đóng form đăng ký, chuyển về form đăng nhập
                setIsRegisterOpen(false); 
                // Tự động điền sẵn email vừa đăng ký vào ô đăng nhập cho tiện
                setEmail(regEmail);
            } else {
                const errorMsg = await response.text();
                alert("Lỗi đăng ký: " + errorMsg);
            }
        } catch (error) {
            console.error("Lỗi kết nối:", error);
            alert("Không thể kết nối đến server!");
        }
    };

    const toggleRegister = (status) => {
        setIsRegisterOpen(status);
    };

    // Xử lý đăng nhập
    // Xử lý đăng nhập bằng API
    const handleLogin = async () => {
        if (!email || !password) {
            alert("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            // Gửi request POST đến Backend (cổng 8081)
            const response = await fetch("http://localhost:8081/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    emailOrPhone: email, // Ghép đúng tên biến LoginRequest.java yêu cầu
                    password: password
                })
            });

            if (response.ok) {
                // Nếu đăng nhập đúng, backend sẽ trả về thông tin user
                const userData = await response.json();
                localStorage.setItem("token", userData.token);
                localStorage.setItem("user", JSON.stringify(userData.user));
                
                // Chuyển hướng sang trang chủ
                navigate('/home'); 
            } else {
                // Đọc thông báo lỗi từ backend (sai pass, không tìm thấy user...)
                const errorMsg = await response.text();
                alert(errorMsg || "Email hoặc mật khẩu không chính xác!");
            }
        } catch (error) {
            console.error("Lỗi khi kết nối API:", error);
            alert("Không thể kết nối đến server. Vui lòng kiểm tra lại Backend!");
        }
    };
    return (
        <div className="container">
            <img className="bg" src={bg} alt="background" />
            
            {/* Logo */}
            <img className="logo-img" src={logo} alt="logo" />
            <div className="logo-text">
                <span className="black">Shop</span><span className="orange">Zone</span>
            </div>

            {/* Text trái */}
            <h1 className="welcome">Chào mừng bạn trở lại!</h1>
            <p className="desc">
                Đăng nhập để khám phá hàng triệu sản phẩm với ưu đãi độc quyền dành riêng cho bạn
            </p>

            {/* Card info */}
            <div className="info-box">
                <div className="info-item">
                    <img className="shipping-img" src={shipping} alt="Free Shipping Icon" />
                    <p><strong>Miễn phí vận chuyển</strong><br /><span>Đơn từ 0đ</span></p>
                </div>
                <div className="info-item">
                    <img className="voucher-img" src={voucher} alt="Voucher Icon" />
                    <p><strong>Voucher hấp dẫn</strong><br /><span>Mỗi ngày</span></p>
                </div>
                <div className="info-item">
                    <img className="payment-img" src={payment} alt="Secure Payment Icon" />
                    <p><strong>Thanh toán an toàn</strong><br /><span>Bảo mật tuyệt đối</span></p>
                </div>
            </div>

            {/* Login form */}
            <div className="login-box">
                <h2>Đăng nhập</h2>
                <p>Chào mừng bạn đến với <span className="orange">ShopZone</span></p>
                <label>Email hoặc số điện thoại:</label>
                <input 
                    type="email" 
                    value={email} 
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="anh@gmail.com"
                />
                <label>Mật khẩu:</label>
                <input 
                    type="password"
                    value={password} 
                    onChange={(e) => setPassword(e.target.value)} 
                    placeholder="123456"
                />
                <a href="#" className="forgot">Quên mật khẩu?</a>
                <button className='login_btn' onClick={handleLogin}>Đăng nhập</button>
                <p className="register">
                    Chưa có tài khoản? <span onClick={() => toggleRegister(true)} style={{cursor: 'pointer'}}>Đăng ký</span>
                </p>
            </div>

            {/* Register form */}
            {isRegisterOpen && (
                <div id="register-overlay" className="overlay">
                    <div className="register-card">
                        <span className="close-btn" onClick={() => toggleRegister(false)}>&times;</span>
                        <h2>Đăng ký</h2>
                        <p className="sub-title">Tạo tài khoản mới</p>
                        
                        <div className="form-group">
                            <input type="text" placeholder="Họ và tên" 
                                value={regFullName} onChange={(e) => setRegFullName(e.target.value)} />
                                
                            <input type="email" placeholder="Email" 
                                value={regEmail} onChange={(e) => setRegEmail(e.target.value)} />
                                
                            <input type="text" placeholder="Số điện thoại" 
                                value={regPhone} onChange={(e) => setRegPhone(e.target.value)} />
                                
                            <input type="password" placeholder="Mật khẩu" 
                                value={regPassword} onChange={(e) => setRegPassword(e.target.value)} />
                                
                            <input type="password" placeholder="Xác nhận mật khẩu" 
                                value={regConfirmPassword} onChange={(e) => setRegConfirmPassword(e.target.value)} />
                                
                            <input type="date" placeholder="Ngày sinh" title="Birthday" 
                                value={regBirthday} onChange={(e) => setRegBirthday(e.target.value)} />
                                
                            <select name="gender" value={regGender} onChange={(e) => setRegGender(e.target.value)}>
                                <option value="" disabled>Giới tính</option>
                                <option value="Male">Nam</option>
                                <option value="Female">Nữ</option>
                            </select>
                        </div>

                        {/* Gắn sự kiện onClick gọi hàm handleRegister */}
                        <button className="btn-register" onClick={handleRegister}>Đăng ký</button>
                        <p className="footer-text">
                            Đã có tài khoản? <span className="orange-link" onClick={() => toggleRegister(false)}>Đăng nhập</span>
                        </p>
                    </div>
                </div>
            )}

            {/* Footer */}
            <div className="footer">
                © 2026 ShopZone.vn - Sàn thương mại điện tử hàng đầu
            </div>
        </div>
    );
}