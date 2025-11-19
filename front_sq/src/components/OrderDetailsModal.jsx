import React from 'react';
import './OrderDetailsModal.css';

function OrderDetailsModal({ table, orders, onClose, onOpenPaymentModal }) { // Changed props
  if (!table) return null;

  // Aggregate all items from all orders for the table
  const aggregateItems = (orderList) => {
    const itemMap = new Map();
    orderList.forEach(order => {
      order.items.forEach(item => {
        if (itemMap.has(item.id)) {
          itemMap.get(item.id).quantity += item.quantity;
        } else {
          itemMap.set(item.id, { ...item });
        }
      });
    });
    return Array.from(itemMap.values());
  };

  const aggregatedItems = aggregateItems(orders);
  const totalAmount = aggregatedItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const handlePaymentClick = () => {
    onOpenPaymentModal(totalAmount); // Open the payment modal with the total amount
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>테이블 {table.number} 주문 내역</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          {aggregatedItems.length > 0 ? (
            <ul className="order-item-list">
              {aggregatedItems.map(item => (
                <li key={item.id} className="order-item-detail">
                  <span className="item-name">{item.name}</span>
                  <span className="item-quantity">{item.quantity}개</span>
                  <span className="item-price">{(item.price * item.quantity).toLocaleString()}원</span>
                </li>
              ))}
            </ul>
          ) : (
            <p>이 테이블에는 아직 주문이 없습니다.</p>
          )}
        </div>
        <div className="modal-footer">
          <div className="total-amount">
            <strong>총 합계:</strong>
            <span>{totalAmount.toLocaleString()}원</span>
          </div>
          <div className="modal-actions">
            <button className="action-button">추가 주문</button>
            <button className="action-button primary" onClick={handlePaymentClick}>결제</button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default OrderDetailsModal;
