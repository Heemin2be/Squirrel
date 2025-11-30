import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axios';
import './ManagementPages.css'; // Assuming shared CSS

function CategoryManagementPage() {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [isAdding, setIsAdding] = useState(false);
  const [newCategoryName, setNewCategoryName] = useState('');
  const [editingCategory, setEditingCategory] = useState(null); // {id, name}
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

    fetchCategories();
  }, [navigate]);

  const fetchCategories = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get('/categories');
      setCategories(response.data);
    } catch (err) {
      console.error("Error fetching categories:", err);
      setError('카테고리 목록을 불러오는 데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('employeeName');
    localStorage.removeItem('role');
    alert('로그아웃 되었습니다.');
    navigate('/');
  };

  const handleAddCategory = async () => {
    if (!newCategoryName.trim()) {
      alert('카테고리 이름을 입력해주세요.');
      return;
    }
    try {
      await apiClient.post('/categories', { name: newCategoryName });
      alert('카테고리가 성공적으로 추가되었습니다.');
      setNewCategoryName('');
      setIsAdding(false);
      fetchCategories();
    } catch (error) {
      console.error('Error adding category:', error);
      alert(`카테고리 추가에 실패했습니다: ${error.response?.data?.message || '오류가 발생했습니다.'}`);
    }
  };

  const handleEditClick = (category) => {
    setEditingCategory(category);
    setNewCategoryName(category.name); // Populate form with current name
  };

  const handleUpdateCategory = async () => {
    if (!newCategoryName.trim()) {
      alert('카테고리 이름을 입력해주세요.');
      return;
    }
    if (!editingCategory) return;

    try {
      await apiClient.put(`/categories/${editingCategory.id}`, { name: newCategoryName });
      alert('카테고리가 성공적으로 수정되었습니다.');
      setEditingCategory(null);
      setNewCategoryName('');
      fetchCategories();
    } catch (error) {
      console.error('Error updating category:', error);
      alert(`카테고리 수정에 실패했습니다: ${error.response?.data?.message || '오류가 발생했습니다.'}`);
    }
  };

  const handleDeleteCategory = async (categoryId) => {
    if (!window.confirm('정말로 이 카테고리를 삭제하시겠습니까? (연결된 메뉴가 있으면 삭제되지 않습니다)')) {
      return;
    }
    try {
      await apiClient.delete(`/categories/${categoryId}`);
      alert('카테고리가 성공적으로 삭제되었습니다.');
      fetchCategories();
    } catch (error) {
      console.error('Error deleting category:', error);
      alert(`카테고리 삭제에 실패했습니다: ${error.response?.data?.message || '오류가 발생했습니다.'}`);
    }
  };

  if (loading) {
    return <div className="management-container"><h1>로딩 중...</h1></div>;
  }

  if (error) {
    return (
      <div className="management-container">
        <header className="management-header">
          <h1>카테고리 관리</h1>
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
        <h1>카테고리 관리</h1>
        <div>
          <button onClick={() => navigate('/pos')}>POS 돌아가기</button>
          <button onClick={handleLogout} className="logout-button">로그아웃</button>
        </div>
      </header>
      
      <div className="actions">
        <button onClick={() => {
          setIsAdding(!isAdding);
          setEditingCategory(null);
          setNewCategoryName('');
        }}>
          {isAdding ? '취소' : '새 카테고리 추가'}
        </button>
      </div>

      {(isAdding || editingCategory) && (
        <div className="form-container">
          <h3>{editingCategory ? '카테고리 수정' : '새 카테고리'}</h3>
          <input
            type="text"
            placeholder="카테고리 이름"
            value={newCategoryName}
            onChange={(e) => setNewCategoryName(e.target.value)}
          />
          <button onClick={editingCategory ? handleUpdateCategory : handleAddCategory}>
            {editingCategory ? '수정하기' : '추가하기'}
          </button>
          {editingCategory && <button onClick={() => {setEditingCategory(null); setNewCategoryName('');}}>취소</button>}
        </div>
      )}

      <div className="data-table">
        <h3>전체 카테고리 목록</h3>
        <ul className="data-list">
          <li className="data-list-header">
            <span>ID</span>
            <span>이름</span>
            <span>관리</span>
          </li>
          {categories.map(cat => (
            <li key={cat.id} className="data-list-item">
              <span>{cat.id}</span>
              <span>{cat.name}</span>
              <span>
                <button className="edit-button" onClick={() => handleEditClick(cat)}>수정</button>
                <button className="delete-button" onClick={() => handleDeleteCategory(cat.id)}>삭제</button>
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default CategoryManagementPage;
