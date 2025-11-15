import React, { useState } from 'react';
import './PosPage.css';

// Placeholder data
const tables = [
  { id: 1, number: '1', status: 'empty' },
  { id: 2, number: '2', status: 'occupied' },
  { id: 3, number: '3', status: 'occupied' },
  { id: 4, number: '4', status: 'empty' },
  { id: 5, number: '5', status: 'empty' },
  { id: 6, number: '6', status: 'occupied' },
  { id: 7, number: '7', status: 'empty' },
  { id: 8, number: '8', status: 'empty' },
];

const recentOrders = [
  { id: 101, table: '2', items: '제육덮밥 x 2, 콜라 x 1', time: '10분 전' },
  { id: 102, table: '6', items: '김치찌개 x 1', time: '5분 전' },
  { id: 103, table: '3', items: '비빔밥 x 4', time: '2분 전' },
];

function PosPage() {
  return (
    <div className="pos-container">
      <header className="pos-header">
        <h1>다람골 POS</h1>
        <nav>
          <a href="#">매출 통계</a>
          <a href="#">메뉴 관리</a>
          <a href="#">직원 관리</a>
        </nav>
      </header>
      <div className="pos-body">
        <main className="table-view">
          <div className="table-grid">
            {tables.map(table => (
              <div key={table.id} className={`table-card ${table.status}`}>
                <div className="table-number">{table.number}</div>
                <div className="table-status">{table.status === 'empty' ? '비어있음' : '식사중'}</div>
              </div>
            ))}
          </div>
        </main>
        <aside className="order-sidebar">
          <h2>최근 주문</h2>
          <ul className="order-list">
            {recentOrders.map(order => (
              <li key={order.id} className="order-item">
                <div className="order-item-header">테이블 {order.table} ({order.time})</div>
                <div className="order-item-body">{order.items}</div>
              </li>
            ))}
          </ul>
        </aside>
      </div>
    </div>
  );
}

export default PosPage;
