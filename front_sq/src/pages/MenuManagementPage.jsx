import React, { useState } from 'react';
import './ManagementPages.css';

// Import images to be used in initial data, similar to KioskPage
import TosokjeonImg from '../assets/tosokjeon.png';
import SaladImg from '../assets/salad.png';
import MukbabImg from '../assets/mukbap.png';
import MukbossamImg from '../assets/mukbossam.png';
import BibimbapImg from '../assets/bibimbap.jpeg';
import ImjatangImg from '../assets/imjatang.jpeg';
import PajeonImg from '../assets/pajeon.jpeg';

// Dummy Data
const initialMenus = [
  { id: 101, name: "토속전", price: 5000, category: "메인 요리", cost: 2000, isSoldOut: false, image: TosokjeonImg },
  { id: 102, name: "임자탕", price: 9000, category: "메인 요리", cost: 3500, isSoldOut: false, image: ImjatangImg },
  { id: 105, name: "도토리파전", price: 12000, category: "메인 요리", cost: 4000, isSoldOut: false, image: PajeonImg },
  { id: 201, name: "계란찜", price: 3000, category: "사이드", cost: 1000, isSoldOut: true, image: `https://via.placeholder.com/150?text=계란찜` },
  { id: 301, name: "콜라", price: 2000, category: "음료", cost: 500, isSoldOut: false, image: `https://via.placeholder.com/150?text=콜라` },
];

const categories = ["메인 요리", "사이드", "음료"];

function MenuManagementPage() {
  const [menus, setMenus] = useState(initialMenus);
  const [isAdding, setIsAdding] = useState(false);
  const [newMenu, setNewMenu] = useState({ name: '', price: '', category: categories[0], cost: '', image: null });

  const handleImageChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setNewMenu({ ...newMenu, image: URL.createObjectURL(file) });
    }
  };

  const handleAddMenu = () => {
    // Dummy implementation
    const menuToAdd = {
        ...newMenu,
        id: Date.now(),
        price: parseInt(newMenu.price, 10),
        cost: parseInt(newMenu.cost, 10),
        isSoldOut: false
    };
    setMenus([...menus, menuToAdd]);
    setIsAdding(false);
    setNewMenu({ name: '', price: '', category: categories[0], cost: '', image: null });
  };

  const handleToggleSoldOut = (id) => {
    setMenus(menus.map(m => m.id === id ? { ...m, isSoldOut: !m.isSoldOut } : m));
  };

  return (
    <div className="management-container">
      <h1>메뉴 관리</h1>
      <div className="actions">
        <button onClick={() => setIsAdding(!isAdding)}>
          {isAdding ? '취소' : '새 메뉴 추가'}
        </button>
      </div>

      {isAdding && (
        <div className="form-container">
          <h3>새 메뉴 정보</h3>
          <input
            type="text"
            placeholder="메뉴 이름"
            value={newMenu.name}
            onChange={(e) => setNewMenu({ ...newMenu, name: e.target.value })}
          />
          <input
            type="number"
            placeholder="가격"
            value={newMenu.price}
            onChange={(e) => setNewMenu({ ...newMenu, price: e.target.value })}
          />
          <input
            type="number"
            placeholder="원가"
            value={newMenu.cost}
            onChange={(e) => setNewMenu({ ...newMenu, cost: e.target.value })}
          />
          <select
            value={newMenu.category}
            onChange={(e) => setNewMenu({ ...newMenu, category: e.target.value })}
          >
            {categories.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
          <div className="file-input-wrapper">
            <label htmlFor="image-upload" className="file-upload-label">이미지 선택</label>
            <input
                id="image-upload"
                type="file"
                accept="image/*"
                onChange={handleImageChange}
            />
            {newMenu.image && <img src={newMenu.image} alt="Preview" className="image-preview" />}
          </div>
          <button onClick={handleAddMenu}>추가하기</button>
        </div>
      )}

      <div className="data-table">
        <h3>전체 메뉴 목록</h3>
        <ul className="data-list">
          <li className="data-list-header">
            <span>메뉴 이름</span>
            <span>가격</span>
            <span>카테고리</span>
            <span>품절 여부</span>
            <span>관리</span>
          </li>
          {menus.map(menu => (
            <li key={menu.id} className="data-list-item">
              <span>{menu.name}</span>
              <span>{menu.price.toLocaleString()}원</span>
              <span>{menu.category}</span>
              <span>{menu.isSoldOut ? '품절' : '판매중'}</span>
              <button onClick={() => handleToggleSoldOut(menu.id)}>
                {menu.isSoldOut ? '판매 개시' : '품절 처리'}
              </button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default MenuManagementPage;
