import React from 'react';
import { useNavigate } from 'react-router-dom';
import './KioskStartPage.css';

function KioskStartPage() {
  const navigate = useNavigate();

  const handleOrderTypeSelect = (type) => {
    navigate('/kiosk-order', { state: { orderType: type } });
  };

  return (
    <div className="kiosk-start-container">
      <h1>주문 방식을 선택해주세요</h1>
      <div className="order-type-options">
        <button className="order-type-button" onClick={() => handleOrderTypeSelect('dinein')}>
          매장 식사
        </button>
        <button className="order-type-button" onClick={() => handleOrderTypeSelect('takeout')}>
          포장
        </button>
      </div>
    </div>
  );
}

export default KioskStartPage;
