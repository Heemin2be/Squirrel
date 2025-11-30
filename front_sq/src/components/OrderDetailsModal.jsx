import React, { useState, useEffect } from 'react';
import apiClient from '../api/axios';
import './OrderDetailsModal.css';

// This is a sub-component to render each individual order
const OrderCard = ({ order, onPay, onCancel }) => {
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
        <button className="cancel-order-button" onClick={() => onCancel(details.orderId)}>
          주문 취소
        </button>
      </div>
    </div>
  );
};

function OrderDetailsModal({ table, orders, onClose, onOpenPaymentModal }) {
  const [currentOrders, setCurrentOrders] = useState(orders);

  useEffect(() => {
    setCurrentOrders(orders);
  }, [orders]);

  const handleCancelOrder = async (orderId) => {
    if (!window.confirm(`주문 번호 ${orderId}를 정말로 취소하시겠습니까?`)) {
      return;
    }
    try {
      await apiClient.delete(`/orders/${orderId}`);
      alert(`주문 번호 ${orderId}가 성공적으로 취소되었습니다.`);
      setCurrentOrders(prev => prev.filter(order => order.orderId !== orderId));
      // Optionally, inform parent (PosPage) to refresh its data (e.g., recentOrders)
      // This will be handled by PosPage.fetchData() being called on modal close.
    } catch (error) {
      console.error('Error canceling order:', error);
      alert(`주문 취소에 실패했습니다: ${error.response?.data?.message || '오류가 발생했습니다.'}`);
    }
  };

  if (!table) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>테이블 {table.tableNumber} 주문 내역</h2>
        </div>
        <div className="modal-body">
          {currentOrders && currentOrders.length > 0 ? (
            currentOrders.map(order => (
              <OrderCard 
                key={order.orderId} 
                order={order} 
                onPay={onOpenPaymentModal} 
                onCancel={handleCancelOrder} 
              />
            ))
          ) : (
            <p>이 테이블에는 결제 대기 중인 주문이 없습니다.</p>
          )}
        </div>
        <div className="modal-footer">
            {currentOrders && currentOrders.length > 0 && (
                <button className="action-button primary" onClick={() => {
                    if (currentOrders.length === 1) {
                        const order = currentOrders[0];
                        onOpenPaymentModal(order.orderId, order.totalPrice);
                    } else {
                        const totalAmount = currentOrders.reduce((sum, order) => sum + order.totalPrice, 0);
                        onOpenPaymentModal(null, totalAmount, table.id);
                    }
                }}>
                    {currentOrders.length > 1 ? '전체 결제' : '결제'}
                </button>
            )}
            <button className="action-button" onClick={onClose}>닫기</button>
        </div>
      </div>
    </div>
  );
}

export default OrderDetailsModal;
