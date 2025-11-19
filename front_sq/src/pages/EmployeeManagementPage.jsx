import React, { useState } from 'react';
import './ManagementPages.css';

// Dummy Data
const initialEmployees = [
  { id: 1, name: '김도훈', pin: '1234', role: '관리자', hourlyWage: 12000 },
  { id: 2, name: '서창민', pin: '5678', role: '직원', hourlyWage: 10000 },
  { id: 3, name: '홍길동', pin: '1111', role: '직원', hourlyWage: 10000 },
];

const roles = ['관리자', '직원'];

function EmployeeManagementPage() {
  const [employees, setEmployees] = useState(initialEmployees);
  const [isAdding, setIsAdding] = useState(false);
  const [newEmployee, setNewEmployee] = useState({ name: '', pin: '', role: roles[1], hourlyWage: '' });

  const handleAddEmployee = () => {
    // Dummy implementation
    const employeeToAdd = {
        ...newEmployee,
        id: Date.now(),
        hourlyWage: parseInt(newEmployee.hourlyWage, 10),
    };
    setEmployees([...employees, employeeToAdd]);
    setIsAdding(false);
    setNewEmployee({ name: '', pin: '', role: roles[1], hourlyWage: '' });
  };

  return (
    <div className="management-container">
      <h1>직원 관리</h1>
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
            <span>PIN</span>
            <span>역할</span>
            <span>시급</span>
          </li>
          {employees.map(emp => (
            <li key={emp.id} className="data-list-item">
              <span>{emp.name}</span>
              <span>{emp.pin}</span>
              <span>{emp.role}</span>
              <span>{emp.hourlyWage.toLocaleString()}원</span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default EmployeeManagementPage;
