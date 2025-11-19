import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Cart from "../components/Cart";
import TableSelectionModal from "../components/TableSelectionModal";
import "./KioskPage.css";

// Image Imports
import TosokjeonImg from '../assets/토속전.png';
import SaladImg from '../assets/샐러드.png';
import MukbabImg from '../assets/묵밥.png';
import MukbossamImg from '../assets/묵보쌈.png';
import BibimbapImg from '../assets/비빔밥.jpeg';
import ImjatangImg from '../assets/임자탕.jpeg';
import PajeonImg from '../assets/파전.jpeg';


// Placeholder data
const categories = [
  { id: 1, name: "메인 요리" },
  { id: 2, name: "사이드" },
  { id: 3, name: "음료" },
];

const menus = {
  1: [
    { id: 101, name: "토속전", price: 5000, image: TosokjeonImg },
    { id: 102, name: "임자탕", price: 9000, image: ImjatangImg },
    { id: 103, name: "비빔밥", price: 8000, image: BibimbapImg },
    { id: 104, name: "샐러드", price: 6000, image: SaladImg },
    { id: 105, name: "도토리파전", price: 12000, image: PajeonImg },
    { id: 106, name: "묵밥", price: 8000, image: MukbabImg },
    { id: 107, name: "묵보쌈", price: 30000, image: MukbossamImg },
  ],
  2: [
    { id: 201, name: "계란찜", price: 3000, image: `https://via.placeholder.com/150?text=계란찜` },
    { id: 202, name: "공기밥", price: 1000, image: `https://via.placeholder.com/150?text=공기밥` },
  ],
  3: [
    { id: 301, name: "콜라", price: 2000, image: `https://via.placeholder.com/150?text=콜라` },
    { id: 302, name: "사이다", price: 2000, image: `https://via.placeholder.com/150?text=사이다` },
  ],
};

const tables = [
    { id: 1, number: "1", status: "empty" },
    { id: 2, number: "2", status: "occupied" },
    { id: 3, number: "3", status: "occupied" },
    { id: 4, number: "4", status: "empty" },
    { id: 5, number: "5", status: "empty" },
    { id: 6, number: "6", status: "occupied" },
    { id: 7, number: "7", status: "empty" },
    { id: 8, number: "8", status: "empty" },
];

function KioskPage() {
  const [activeCategory, setActiveCategory] = useState(categories[0].id);
  const [cart, setCart] = useState([]);
  const [isTableModalOpen, setIsTableModalOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { orderType } = location.state || { orderType: 'dinein' }; // Default to dinein if not specified

  const handleAddToCart = (menu) => {
    setCart((prevCart) => {
      const existingItem = prevCart.find((item) => item.id === menu.id);
      if (existingItem) {
        return prevCart.map((item) =>
          item.id === menu.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      }
      return [...prevCart, { ...menu, quantity: 1 }];
    });
  };

  const handleUpdateQuantity = (menuId, newQuantity) => {
    if (newQuantity <= 0) {
      handleRemoveItem(menuId);
    } else {
      setCart((prevCart) =>
        prevCart.map((item) =>
          item.id === menuId ? { ...item, quantity: newQuantity } : item
        )
      );
    }
  };

  const handleRemoveItem = (menuId) => {
    setCart((prevCart) => prevCart.filter((item) => item.id !== menuId));
  };

  const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const handleCheckout = () => {
    if (cart.length === 0) {
      alert('장바구니에 상품을 담아주세요.');
      return;
    }

    if (orderType === 'takeout') {
      const orderNumber = Math.floor(Math.random() * 900) + 100;
      console.log("포장 주문 완료:", { orderNumber, items: cart, total });
      alert(`주문이 성공적으로 완료되었습니다.\n주문번호: ${orderNumber}`);
      navigate('/kiosk', { replace: true }); // Go back to KioskStartPage
    } else { // Dine-in
      setIsTableModalOpen(true);
    }
  };

  const handleTableSelect = (table) => {
    if (table.status === 'occupied') {
        alert('이미 사용중인 테이블입니다. 다른 테이블을 선택해주세요.');
        return;
    }
    setIsTableModalOpen(false);
    
    const orderNumber = Math.floor(Math.random() * 900) + 100;
    console.log(`테이블 ${table.number} 매장 주문 완료:`, { orderNumber, table: table.number, items: cart, total });
    alert(`주문이 성공적으로 완료되었습니다.\n주문하신 메뉴는 테이블 ${table.number}로 가져다 드리겠습니다.`);
    navigate('/kiosk', { replace: true }); // Go back to KioskStartPage
  };

  return (
    <div className="kiosk-container">
      <div className="kiosk-body">
        <aside className="sidebar">
          <h2>카테고리</h2>
          <ul className="category-list">
            {categories.map((cat) => (
              <li key={cat.id}>
                <button
                  className={`category-button ${
                    activeCategory === cat.id ? "active" : ""
                  }`}
                  onClick={() => setActiveCategory(cat.id)}
                >
                  {cat.name}
                </button>
              </li>
            ))}
          </ul>
        </aside>
        <main className="main-content">
          <h2 className="order-type-display">
            {orderType === 'dinein' ? '매장 식사' : '포장'}
          </h2>
          <div className="menu-grid">
            {menus[activeCategory].map((menu) => (
              <div key={menu.id} className="menu-card" onClick={() => handleAddToCart(menu)}>
                <img
                  src={menu.image}
                  alt={menu.name}
                />
                <h3>{menu.name}</h3>
                <p>{menu.price.toLocaleString()}원</p>
              </div>
            ))}
          </div>
        </main>
        <Cart
          cart={cart}
          onUpdateQuantity={handleUpdateQuantity}
          onRemoveItem={handleRemoveItem}
        />
      </div>
      <footer className="kiosk-footer">
        <div className="order-summary">
          총 주문 금액: {total.toLocaleString()}원
        </div>
        <button className="checkout-button" onClick={handleCheckout}>주문하기</button>
      </footer>
      {isTableModalOpen && (
        <TableSelectionModal
          tables={tables}
          onSelect={handleTableSelect}
          onClose={() => setIsTableModalOpen(false)}
        />
      )}
    </div>
  );
}

export default KioskPage;
