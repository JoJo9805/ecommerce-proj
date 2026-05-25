import React, { useState, useEffect, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import "../../styles/seller/ShopInvoice.css";
import productImg from '../public/assets/muado.jpg';

export default function ShopInvoice() {
  const { id } = useParams();

  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchText, setSearchText] = useState("");
  const [searchKeyword, setSearchKeyword] = useState("");

  // Hiển thị hóa đơn của Shop
  useEffect(() => {
    if (!id) return;
    const token = localStorage.getItem("token");
    setLoading(true);
    fetch(`http://localhost:8081/api/orders/shop/${id}`, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    })
      .then((res) => {
        if (!res.ok) throw new Error("Không thể tải dữ liệu hóa đơn của shop");
        return res.json();
      })
      .then((data) => {
        const formattedOrders = data.map((dto) => {
          const order = dto.order || {};
          const items = dto.items || [];

          const productNames = items
            .map(item => item.productVariant?.product?.productName || "Sản phẩm")
            .join(", ");

          const totalAmount = items.reduce((sum, item) => {
            return sum + ((item.price || 0) * (item.quantity || 0));
          }, 0);

          return {
            id: order.orderID,
            invoiceCode: `DH${String(order.orderID).padStart(5, '0')}`, 
            customerName: order.address?.receiverName || order.buyer?.fullName || "Khách hàng",
            customerPhone: order.address?.phone || "N/A",
            detailAddress: order.address?.detailAddress || "",
            productName: productNames || "Không có thông tin sản phẩm",
            totalPrice: totalAmount,
            paymentStatus: order.paymentStatus,
            shippingStatus: order.shippingStatus,
            orderDate: order.orderDate ? new Date(order.orderDate).toLocaleDateString("vi-VN") : "---",
            rawItems: items
          };
        });

        setInvoices(formattedOrders);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, [id]);

  const getShippingStatusText = (status) => {
    switch (status) {
      case "Pending": return "Chờ xử lý";
      case "Shipping": return "Đang giao hàng";
      case "Confirmed": return "Đã nhận";
      default: return status || "Chưa rõ";
    }
  };

  // Logic tìm kiếm
  const filteredInvoices = useMemo(() => {
    if (!searchKeyword.trim()) return invoices;

    return invoices.filter((inv) => {
      const keyword = searchKeyword.toLowerCase();
      return (
        inv.invoiceCode.toLowerCase().includes(keyword) ||
        inv.customerName.toLowerCase().includes(keyword) ||
        inv.customerPhone.toLowerCase().includes(keyword) ||
        inv.productName.toLowerCase().includes(keyword) ||
        inv.orderDate.toLowerCase().includes(keyword)
      );
    });
  }, [searchKeyword, invoices]);

  const handleSearch = () => {
    setSearchKeyword(searchText);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  if (loading) return <div className="main-content-admin"><p>Đang tải danh sách hóa đơn...</p></div>;
  if (error) return <div className="main-content-admin"><p style={{ color: "red" }}>Lỗi: {error}</p></div>;

  return (
    <div className="main-content-admin">
        <div className="invoice-container">
            <div className="invoice-header">
              <h2 className="invoice-title">Hóa Đơn</h2>
              <div className="search-bar">
                <input
                  type="text"
                  placeholder="Search"
                  value={searchText}
                  onChange={(e) => setSearchText(e.target.value)}
                  onKeyDown={handleKeyDown}
                />

                <i
                  className="bx bx-search"
                  onClick={handleSearch}
                ></i>
              </div>
            </div>

            <div className="invoice-table-wrapper">
              <table className="invoice-table">
                  <thead>
                      <tr>
                      <th>Mã đơn hàng</th>
                        <th>Tên người nhận</th>
                        <th>Số điện thoại</th>
                        <th>Sản phẩm đặt</th>
                        <th>Tổng thanh toán</th>
                        <th>Ngày đặt</th>
                        <th>Trạng thái giao hàng</th>
                      </tr>
                  </thead>
                  <tbody>
                      {filteredInvoices.map((inv, index) => (
                      <tr
                          key={inv.id}
                          data-status={inv.status}
                          style={{ cursor: "pointer" }}
                      >
                          <td>{inv.invoiceCode}</td>
                          <td>{inv.customerName}</td>
                          <td>{inv.customerPhone}</td>
                          <td>{inv.productName}</td>
                          <td>{inv.totalPrice.toLocaleString("vi-VN")} đ</td>
                          <td>{inv.orderDate}</td>
                          <td>
                            <span className={`status-badge ${inv.shippingStatus?.toLowerCase()}`}>
                              {getShippingStatusText(inv.shippingStatus)}
                            </span>
                          </td>
                      </tr>
                      ))}
                  </tbody>
              </table>
            </div>
      </div>
    </div>
  );
}
