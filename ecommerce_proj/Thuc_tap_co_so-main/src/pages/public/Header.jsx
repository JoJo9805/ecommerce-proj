import React, { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom' 
import '../../styles/public/Header.css';
import logo from './assets/sp-logo-2.png';

export default function Header() {
  const [searchTerm, setSearchTerm] = useState(""); 
  const [notifications, setNotifications] = useState([]); // State chứa danh sách thông báo
  const navigate = useNavigate(); 

  // KÉO DỮ LIỆU THÔNG BÁO TỪ BACKEND
  useEffect(() => {
    const fetchNotifications = async () => {
      const userStr = localStorage.getItem('user');
      const token = localStorage.getItem('token');

      if (userStr && token) {
        const user = JSON.parse(userStr);
        const userId = user.userID || user.userid || user.id;

        try {
          const response = await fetch(`http://localhost:8081/api/notifications/user/${userId}`, {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              "Authorization": `Bearer ${token}` // Kẹp thẻ thông hành
            }
          });

          if (response.ok) {
            const data = await response.json();
            setNotifications(data);
          }
        } catch (error) {
          console.error("Lỗi khi tải thông báo:", error);
        }
      }
    };

    fetchNotifications();
    const timer = setInterval(fetchNotifications, 30000); 
    window.addEventListener('focus', fetchNotifications); 

    return () => {
      clearInterval(timer);
      window.removeEventListener('focus', fetchNotifications);
    };
  }, []);

  const handleSearch = () => {
    if (searchTerm.trim() !== "") {
      navigate(`/home?search=${encodeURIComponent(searchTerm)}`);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleSearch();
    }
  };

  // Đếm số lượng thông báo CHƯA ĐỌC (isRead = false) để hiện chấm đỏ
  const unreadCount = notifications.filter(n => !n.isRead).length;

  const handleMarkAllRead = async () => {
    if (unreadCount === 0) return; // không có gì chưa đọc thì thôi

    const userStr = localStorage.getItem('user');
    const token = localStorage.getItem('token');
    if (!userStr || !token) return;

    const user = JSON.parse(userStr);
    const userId = user.userID || user.userid || user.id;

    // Cập nhật giao diện ngay cho mượt (chấm đỏ biến mất tức thì)
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));

    try {
      await fetch(`http://localhost:8081/api/notifications/user/${userId}/read-all`, {
        method: "PUT",
        headers: { "Authorization": `Bearer ${token}` }
      });
    } catch (error) {
      console.error("Lỗi khi đánh dấu đã đọc:", error);
    }
  };

  return (
    <header className="header">
      {/* Logo Section */}
      <div className="header__logo" >
        <Link to="/home"
          className="header__logo-link"
          onClick={(e) => {
            setSearchTerm("");
            if (window.location.pathname === "/home") {
              e.preventDefault();
              window.location.reload();
            }
          }}
        >
          <h2 className="header__logo-title">
            <img src={logo} alt="logo" className="header__logo-img"/>
            ShopZone
          </h2>
        </Link>
      </div>
      
      {/* Search Section */}
      <div className="header__search">
        <input 
          type="text" 
          placeholder="Nhập tên sản phẩm, thương hiệu ..." 
          className="header__search-input"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)} 
          onKeyDown={handleKeyDown} 
        />
        <i 
          className='bx bx-search header__search-icon' 
          onClick={handleSearch} 
        ></i>
      </div>

      {/* Actions/Options Section */}
      <div className="header__actions">
        {/* Notifications */}
        <div className="header__tooltip-container" onMouseEnter={handleMarkAllRead}>
          <div style={{ position: 'relative' }}>
            <i className='bx bx-bell header__icon'></i>
            {/* Hạt thông báo đỏ (chỉ hiện khi có thông báo chưa đọc) */}
            {unreadCount > 0 && (
              <span style={{
                position: 'absolute', top: '-5px', right: '-5px', 
                background: 'white', color: '#ee4d2d', borderRadius: '50%', 
                padding: '2px 6px', fontSize: '10px', fontWeight: 'bold'
              }}>
                {unreadCount}
              </span>
            )}
          </div>
          
          <div className="header__notifications">
            <p className="header__notifications-header">Thông báo của bạn</p>
            <div className="header__notifications-scroll">
              <div className="header__notifications-list">
                
                {/* RENDER DANH SÁCH THÔNG BÁO TỪ API */}
                {notifications.length > 0 ? (
                  notifications.map((notif, index) => (
                    <div
                      key={notif.notificationId || index}
                      className={`notify-card ${!notif.isRead ? 'notify-card--unread' : ''}`}
                    >
                      <div className="notify-card__title">{notif.title}</div>
                      <div className="notify-card__content">{notif.content}</div>
                      <div className="notify-card__date">
                        {notif.createdAt
                          ? new Date(notif.createdAt).toLocaleString('vi-VN')
                          : 'Vừa xong'}
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="notify-empty">Bạn chưa có thông báo nào.</div>
                )}
                
              </div>
            </div>
          </div>
        </div>
        
        {/* Cart */}
        <Link to="/cart" className="header__action-link">
          <div className="header__tooltip-container">
            <i className='bx bx-cart header__icon'></i>
            <span className="header__tooltip-text">Giỏ hàng</span>
          </div>
        </Link>
        
        {/* Account */}
        <Link to="/account" className="header__action-link">
          <div className="header__tooltip-container">
            <i className='bx bx-user header__icon'></i>
            <span className="header__tooltip-text">Trang cá nhân</span>
          </div>
        </Link>
      </div>
    </header>
  )
}