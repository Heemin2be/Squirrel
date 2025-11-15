import React, { useState } from 'react';
import './KioskPage.css';

// Placeholder data - In a real app, this would come from an API call
const categories = [
  { id: 1, name: '메인 요리' },
  { id: 2, name: '사이드' },
  { id: 3, name: '음료' },
];

const menus = {
  1: [
    { id: 101, name: '제육덮밥', price: 8000 },
    { id: 102, name: '김치찌개', price: 7500 },
    { id: 103, name: '된장찌개', price: 7500 },
    { id: 104, name: '비빔밥', price: 7000 },
  ],
  2: [
    { id: 201, name: '계란찜', price: 3000 },
    { id: 202, name: '공기밥', price: 1000 },
  ],
  3: [
    { id: 301, name: '콜라', price: 2000 },
    { id: 302, name: '사이다', price: 2000 },
  ],
};

function KioskPage() {
  const [activeCategory, setActiveCategory] = useState(categories[0].id);
  const [cart, setCart] = useState([]);

  const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <div className="kiosk-container">
      <div className="kiosk-body">
        <aside className="sidebar">
          <h2>카테고리</h2>
          <ul className="category-list">
            {categories.map((cat) => (
              <li key={cat.id}>
                <button
                  className={`category-button ${activeCategory === cat.id ? 'active' : ''}`}
                  onClick={() => setActiveCategory(cat.id)}
                >
                  {cat.name}
                </button>
              </li>
            ))}
          </ul>
        </aside>
        <main className="main-content">
          <div className="menu-grid">
            {menus[activeCategory].map((menu) => (
              <div key={menu.id} className="menu-card">
                <img src={`https://via.placeholder.com/150?text=${menu.name}`} alt={menu.name} />
                <h3>{menu.name}</h3>
                <p>{menu.price.toLocaleString()}원</p>
              </div>
            ))}
          </div>
        </main>
      </div>
      <footer className="kiosk-footer">
        <div className="order-summary">
          총 주문 금액: {total.toLocaleString()}원
        </div>
        <button className="checkout-button">주문하기</button>
      </footer>
    </div>
  );
}

export default KioskPage;
