import React, { useState } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import KioskPage from './pages/KioskPage';
import PosPage from './pages/PosPage';
import SalesStatisticsPage from './pages/SalesStatisticsPage';
import MenuManagementPage from './pages/MenuManagementPage';
import EmployeeManagementPage from './pages/EmployeeManagementPage';
import KioskStartPage from './pages/KioskStartPage';
import Root from './pages/Root';
import PinModal from './components/PinModal';
import './App.css';

function App() {
  const navigate = useNavigate();
  const [isPinModalOpen, setIsPinModalOpen] = useState(false);
  const [targetPath, setTargetPath] = useState('');

  const handlePinSubmit = (pin) => {
    if (pin === '0000') { // Hardcoded admin PIN
      setIsPinModalOpen(false);
      navigate(targetPath);
    } else {
      alert('잘못된 PIN 번호입니다.');
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
          <Route path="/kiosk-order" element={<KioskPage />} />
          <Route path="/pos" element={<PosPage onRequirePin={handleRequirePin} />} />
          {/* <Route path="/checkout" element={<CheckoutPage />} /> Removed CheckoutPage */}
          <Route path="/pos/sales" element={<SalesStatisticsPage />} />
          <Route path="/pos/menu" element={<MenuManagementPage />} />
          <Route path="/pos/employees" element={<EmployeeManagementPage />} />
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
