import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "../../styles/seller/ShopHome.css";
import logo from '../public/assets/sp-logo-2.png';
import ShopProfile from "./ShopProfile";
import ShopInvoice from "./ShopInvoice";

export default function ShopHome() {
    const { id, active_tab } = useParams();
    const navigate = useNavigate(); 
    const activeTab = active_tab || "profile";
    const [shopInfo, setShopInfo] = useState(null);

    const handleTabChange = (tabName) => {
        navigate(`/seller/${tabName}/${id}`);
    };

    useEffect(() => {
        const fetchShop = async () => {
            try {
                const response = await fetch(`http://localhost:8081/api/shops/${id}`);
                if (!response.ok) {
                    throw new Error("Không tải được shop");
                }
                const data = await response.json();
                setShopInfo(data);
            } catch (err) {
                console.log(err);
            }
        };
        if (id) {fetchShop();}
    }, [id]);

    const renderContent = () => {
        switch (activeTab) {
            case "profile":
                return (
                    <div className="main-content">
                        <ShopProfile />
                    </div>
                );
            case "invoice":
                return (
                    <div className="main-content">
                        <ShopInvoice />
                    </div>
                );
            default:
                return <div className="main-content" />;
        }
    };

    return (
        <>
        <div className="shopHome-navbar">
            <div className="shopHome-header">
                <h2 className="shopHome-logo-title">
                    <img src={logo} alt="logo" className="header__logo-img"/>
                    ShopZone
                </h2>
            </div>

            <div
                className={`profile ${activeTab === "profile" ? "active-link" : ""}`}
                onClick={() => handleTabChange("profile")}
            >
                Thông tin cửa hàng
            </div>

            <div
                className={`invoice ${activeTab === "invoice" ? "active-link" : ""}`}
                onClick={() => handleTabChange("invoice")}
            >
                Hóa đơn bán hàng
            </div>

            <div
                className={`buyer ${activeTab === "buyer" ? "active-link" : ""}`}
                onClick={() => navigate(`/account/profile`)}
            >
                Chuyển về trang mua sắm
            </div>

            <div
                className={`logout ${activeTab === "logout" ? "active-link" : ""}`}
                onClick={() => {
                        localStorage.removeItem("token");
                        localStorage.removeItem("user");
                        alert("Đăng xuất thành công!");
                        navigate("/login");
                    }}
            >
                Đăng xuất
            </div>
        </div>

        {renderContent()}
        </>
    );
}