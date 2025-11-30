import React, { useState } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import apiClient from './api/axios';
import KioskPage from './pages/KioskPage';
import PosPage from './pages/PosPage';
import SalesStatisticsPage from './pages/SalesStatisticsPage';
import MenuManagementPage from './pages/MenuManagementPage';
import EmployeeManagementPage from './pages/EmployeeManagementPage';
import CategoryManagementPage from './pages/CategoryManagementPage';
import KioskStartPage from './pages/KioskStartPage';
import Root from './pages/Root';
import PinModal from './components/PinModal';
import './App.css';

function App() {
  const navigate = useNavigate();
  const [isPinModalOpen, setIsPinModalOpen] = useState(false);
  const [targetPath, setTargetPath] = useState('');

  const handlePinSubmit = async (pin) => {
    try {
      const response = await apiClient.post('/auth/login', { pin });
      const { accessToken, employeeName, role } = response.data;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('employeeName', employeeName);
      localStorage.setItem('role', role);

      alert(`${employeeName}님, 환영합니다.`);
      setIsPinModalOpen(false);
      navigate(targetPath);
    } catch (error) {
      console.error('Login failed:', error);
      alert('로그인에 실패했습니다. PIN 번호를 확인해주세요.');
    }
  };

  const handleRequirePin = (path) => {
    setTargetPath(path);
    setIsPinModalOpen(true);
  };

  return (
    <div className="App">
      <main>
        <Routes>
          <Route path="/" element={<Root />} />
          <Route path="/kiosk" element={<KioskStartPage />} />
          <Route path="/kiosk-order/:tableId" element={<KioskPage />} />
          <Route path="/pos" element={<PosPage onRequirePin={handleRequirePin} />} />
          {/* <Route path="/checkout" element={<CheckoutPage />} /> Removed CheckoutPage */}
          <Route path="/pos/sales" element={<SalesStatisticsPage />} />
          <Route path="/pos/menu" element={<MenuManagementPage />} />
          <Route path="/pos/employees" element={<EmployeeManagementPage />} />
          <Route path="/pos/categories" element={<CategoryManagementPage />} />
        </Routes>
      </main>

      {isPinModalOpen && (
        <PinModal
          onPinSubmit={handlePinSubmit}
          onClose={() => setIsPinModalOpen(false)}
          message="관리자 PIN을 입력해주세요."
        />
      )}
    </div>
  );
}

export default App;