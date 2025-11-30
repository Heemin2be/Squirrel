import React, { useState, useEffect } from 'react';
import apiClient from '../api/axios';
import './OrderDetailsModal.css';

// This is a sub-component to render each individual order
const OrderCard = ({ order, onPay }) => {
  const [details, setDetails] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchOrderDetails = async () => {
      try {
        setLoading(true);
        const response = await apiClient.get(`/orders/${order.orderId}`);
        setDetails(response.data);
      } catch (error) {
        console.error(`Error fetching details for order ${order.orderId}`, error);
      } finally {
        setLoading(false);
      }
    };
    fetchOrderDetails();
  }, [order.orderId]);

  if (loading) {
    return <div className="order-card loading">주문 상세 로딩...</div>;
  }

  if (!details) {
    return <div className="order-card error">주문 정보를 불러오지 못했습니다.</div>;
  }

  return (
    <div className="order-card">
      <h4>주문 번호: {details.orderId} ({new Date(details.orderTime).toLocaleTimeString()})</h4>
      <ul className="order-item-list">
        {details.items.map(item => (
          <li key={item.menuId} className="order-item-detail">
            <span className="item-name">{item.menuName}</span>
            <span className="item-quantity">{item.quantity}개</span>
            <span className="item-price">{(item.price * item.quantity).toLocaleString()}원</span>
          </li>
        ))}
      </ul>
      <div className="order-card-footer">
        <strong>총 합계: {details.totalPrice.toLocaleString()}원</strong>
        <button className="action-button primary" onClick={() => onPay(details.orderId, details.totalPrice)}>
          결제
        </button>
      </div>
    </div>
  );
};

function OrderDetailsModal({ table, orders, onClose, onOpenPaymentModal }) {
  if (!table) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>테이블 {table.tableNumber} 주문 내역</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          {orders && orders.length > 0 ? (
            orders.map(order => (
              <OrderCard key={order.orderId} order={order} onPay={onOpenPaymentModal} />
            ))
          ) : (
            <p>이 테이블에는 결제 대기 중인 주문이 없습니다.</p>
          )}
        </div>
      </div>
    </div>
  );
}

export default OrderDetailsModal;
