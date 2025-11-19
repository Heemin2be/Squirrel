import React from 'react';
import './ManagementPages.css';

// Dummy Data
const salesData = {
  today: {
    totalSales: 1250000,
    totalOrders: 80,
    salesByMenu: [
      { name: '임자탕', quantity: 50, total: 450000 },
      { name: '묵보쌈', quantity: 15, total: 450000 },
      { name: '도토리파전', quantity: 25, total: 300000 },
    ],
  },
  weekly: {
    totalSales: 7800000,
    totalOrders: 550,
  },
  monthly: {
    totalSales: 31200000,
    totalOrders: 2100,
  },
};

function SalesStatisticsPage() {
  return (
    <div className="management-container">
      <h1>매출 통계</h1>
      <div className="stats-section">
        <h2>금일 현황</h2>
        <div className="stats-card">
          <p><strong>총 매출:</strong> {salesData.today.totalSales.toLocaleString()}원</p>
          <p><strong>총 주문 수:</strong> {salesData.today.totalOrders}건</p>
        </div>
        <h3>메뉴별 판매 순위 (오늘)</h3>
        <ul className="data-list">
          {salesData.today.salesByMenu.map(item => (
            <li key={item.name} className="data-list-item">
              <span>{item.name}</span>
              <span>{item.quantity}개</span>
              <span>{item.total.toLocaleString()}원</span>
            </li>
          ))}
        </ul>
      </div>

      <div className="stats-section">
        <h2>기간별 요약</h2>
        <div className="stats-card-group">
          <div className="stats-card">
            <h3>주간 매출</h3>
            <p>{salesData.weekly.totalSales.toLocaleString()}원</p>
            <span>(총 {salesData.weekly.totalOrders}건)</span>
          </div>
          <div className="stats-card">
            <h3>월간 매출</h3>
            <p>{salesData.monthly.totalSales.toLocaleString()}원</p>
            <span>(총 {salesData.monthly.totalOrders}건)</span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SalesStatisticsPage;
