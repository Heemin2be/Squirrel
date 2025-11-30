import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axios';
import './ManagementPages.css';

const roles = ['ADMIN', 'STAFF'];

function EmployeeManagementPage() {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const [isAdding, setIsAdding] = useState(false);
  const [newEmployee, setNewEmployee] = useState({ name: '', pin: '', role: roles[1], hourlyWage: '' });
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

    const fetchEmployees = async () => {
      setLoading(true);
      try {
        const response = await apiClient.get('/employees');
        setEmployees(response.data);
      } catch (err) {
        console.error("Error fetching employees:", err);
        setError('직원 목록을 불러오는 데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchEmployees();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('employeeName');
    localStorage.removeItem('role');
    alert('로그아웃 되었습니다.');
    navigate('/');
  };

  const handleAddEmployee = async () => {
    if (!newEmployee.name || !newEmployee.pin || !newEmployee.hourlyWage) {
      alert('모든 필드를 채워주세요.');
      return;
    }

    const employeeData = {
      name: newEmployee.name,
      pin: newEmployee.pin,
      role: `ROLE_${newEmployee.role}`, // Ensure ROLE_ prefix
      hourlyWage: parseInt(newEmployee.hourlyWage, 10),
    };

    try {
      await apiClient.post('/employees', employeeData);
      alert('직원이 성공적으로 추가되었습니다.');
      setIsAdding(false);
      setNewEmployee({ name: '', pin: '', role: roles[1], hourlyWage: '' });
      const response = await apiClient.get('/employees'); // Re-fetch
      setEmployees(response.data);
    } catch (error) {
      console.error('Error adding employee:', error);
      alert(`직원 추가에 실패했습니다: ${error.response?.data?.message || '오류가 발생했습니다.'}`);
    }
  };

  const handleDeleteEmployee = async (employeeId) => {
    if (!window.confirm('정말로 이 직원을 삭제하시겠습니까?')) {
      return;
    }

    try {
      await apiClient.delete(`/employees/${employeeId}`);
      alert('직원이 성공적으로 삭제되었습니다.');
      // Re-fetch employees to update the list
      const response = await apiClient.get('/employees');
      setEmployees(response.data);
    } catch (error) {
      console.error('Error deleting employee:', error);
      alert(`직원 삭제에 실패했습니다: ${error.response?.data?.message || '오류가 발생했습니다.'}`);
    }
  };
  
  if (loading) {
    return <div className="management-container"><h1>로딩 중...</h1></div>;
  }

  if (error) {
    return (
      <div className="management-container">
        <header className="management-header">
          <h1>직원 관리</h1>
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
        <h1>직원 관리</h1>
        <div>
          <button onClick={() => navigate('/pos')}>POS 돌아가기</button>
          <button onClick={handleLogout} className="logout-button">로그아웃</button>
        </div>
      </header>
      
      <div className="actions">
        <button onClick={() => setIsAdding(!isAdding)}>
          {isAdding ? '취소' : '새 직원 추가'}
        </button>
      </div>

      {isAdding && (
        <div className="form-container">
          <h3>새 직원 정보</h3>
          <input
            type="text"
            placeholder="이름"
            value={newEmployee.name}
            onChange={(e) => setNewEmployee({ ...newEmployee, name: e.target.value })}
          />
          <input
            type="text"
            placeholder="PIN (4자리)"
            maxLength="4"
            value={newEmployee.pin}
            onChange={(e) => setNewEmployee({ ...newEmployee, pin: e.target.value })}
          />
          <input
            type="number"
            placeholder="시급"
            value={newEmployee.hourlyWage}
            onChange={(e) => setNewEmployee({ ...newEmployee, hourlyWage: e.target.value })}
          />
          <select
            value={newEmployee.role}
            onChange={(e) => setNewEmployee({ ...newEmployee, role: e.target.value })}
          >
            {roles.map(r => <option key={r} value={r}>{r}</option>)}
          </select>
          <button onClick={handleAddEmployee}>추가하기</button>
        </div>
      )}

      <div className="data-table">
        <h3>전체 직원 목록</h3>
        <ul className="data-list">
          <li className="data-list-header">
            <span>이름</span>
            <span>역할</span>
            <span>시급</span>
            <span>관리</span>
          </li>
          {employees.map(emp => (
            <li key={emp.id} className="data-list-item">
              <span>{emp.name}</span>
              <span>{emp.role.replace('ROLE_', '')}</span>
              <span>{emp.hourlyWage.toLocaleString()}원</span>
              <span>
                <button className="delete-button" onClick={() => handleDeleteEmployee(emp.id)}>삭제</button>
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default EmployeeManagementPage;
