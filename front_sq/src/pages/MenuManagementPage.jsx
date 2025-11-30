import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axios';
import './ManagementPages.css';

function MenuManagementPage() {
  const navigate = useNavigate();
  const [menus, setMenus] = useState([]);
  const [categories, setCategories] = useState([]);
  const [isAdding, setIsAdding] = useState(false);
  const [newMenu, setNewMenu] = useState({ name: '', price: '', categoryId: '', cost: '', imageUrl: null });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      alert('접근 권한이 없습니다. 로그인해주세요.');
      navigate('/pos');
      return;
    }

    const userRole = localStorage.getItem('role');
    if (userRole !== 'ROLE_ADMIN') {
      setError('관리자 전용 페이지입니다.');
      setLoading(false);
      return;
    }
    
    const fetchInitialData = async () => {
      setLoading(true);
      try {
        const [menusResponse, categoriesResponse] = await Promise.all([
          apiClient.get('/menus'),
          apiClient.get('/categories')
        ]);
        setMenus(menusResponse.data);
        setCategories(categoriesResponse.data);
        if (categoriesResponse.data.length > 0) {
          setNewMenu(prev => ({ ...prev, categoryId: categoriesResponse.data[0].id }));
        }
      } catch (err) {
        console.error("Error fetching initial data:", err);
        setError('데이터를 불러오는 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchInitialData();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('employeeName');
    localStorage.removeItem('role');
    alert('로그아웃 되었습니다.');
    navigate('/');
  };

  const handleImageChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setNewMenu({ ...newMenu, imageUrl: URL.createObjectURL(file) });
    }
  };

  const handleAddMenu = async () => {
    if (!newMenu.name || !newMenu.price || !newMenu.cost || !newMenu.categoryId) {
      alert('모든 필드를 채워주세요.');
      return;
    }

    const menuData = {
      name: newMenu.name,
      price: parseInt(newMenu.price, 10),
      cost: parseInt(newMenu.cost, 10),
      categoryId: newMenu.categoryId,
      imageUrl: null, // Image upload is not implemented in this version
    };

    try {
      await apiClient.post('/menus', menuData);
      alert('메뉴가 성공적으로 추가되었습니다.');
      setIsAdding(false);
      setNewMenu({ name: '', price: '', categoryId: categories.length > 0 ? categories[0].id : '', cost: '', imageUrl: null });
      const response = await apiClient.get('/menus'); // Re-fetch
      setMenus(response.data);
    } catch (error) {
      console.error('Error adding menu:', error);
      alert(`메뉴 추가에 실패했습니다: ${error.response?.data?.message || '관리자 권한이 필요합니다'}`);
    }
  };

  const handleToggleSoldOut = async (id, currentStatus) => {
    const newStatus = !currentStatus;
    try {
      await apiClient.patch(`/menus/${id}`, { isSoldOut: newStatus });
      
      setMenus(prevMenus => 
        prevMenus.map(menu => 
          menu.id === id ? { ...menu, isSoldOut: newStatus } : menu
        )
      );
      
      alert(`상태가 '${newStatus ? '품절' : '판매중'}'으로 변경되었습니다.`);
    } catch (error) {
      console.error('Error updating sold out status:', error);
      alert(`상태 변경에 실패했습니다: ${error.response?.data?.message || '관리자 권한이 필요합니다'}`);
    }
  };

  if (loading) {
    return <div className="management-container"><h1>로딩 중...</h1></div>;
  }

  if (error) {
    return (
      <div className="management-container">
        <header className="management-header">
          <h1>메뉴 관리</h1>
          <div>
            <button onClick={() => navigate('/pos')}>POS 돌아가기</button>
            <button onClick={handleLogout} className="logout-button">로그아웃</button>
          </div>
        </header>
        <div className="error-message">{error}</div>
      </div>
    );
  }

  return (
    <div className="management-container">
      <header className="management-header">
        <h1>메뉴 관리</h1>
        <div>
          <button onClick={() => navigate('/pos')}>POS 돌아가기</button>
          <button onClick={handleLogout} className="logout-button">로그아웃</button>
        </div>
      </header>

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
            value={newMenu.categoryId}
            onChange={(e) => setNewMenu({ ...newMenu, categoryId: e.target.value })}
          >
            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
          <div className="file-input-wrapper">
            <label htmlFor="image-upload" className="file-upload-label">이미지 선택</label>
            <input
                id="image-upload"
                type="file"
                accept="image/*"
                onChange={handleImageChange}
            />
            {newMenu.imageUrl && <img src={newMenu.imageUrl} alt="Preview" className="image-preview" />}
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
              <span>{menu.categoryName}</span>
              <span className={menu.isSoldOut ? 'status-text-sold-out' : 'status-text-available'}>{menu.isSoldOut ? '품절' : '판매중'}</span>
              <button 
                onClick={() => handleToggleSoldOut(menu.id, menu.isSoldOut)}
                className={menu.isSoldOut ? 'status-available' : 'status-sold-out'}
              >
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
