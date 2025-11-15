import { Routes, Route, Link } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import KioskPage from './pages/KioskPage';
import PosPage from './pages/PosPage';
import './App.css';

function App() {
  return (
    <div className="App">
      <nav style={{ marginBottom: '20px', borderBottom: '1px solid #ccc', paddingBottom: '10px' }}>
        <Link to="/" style={{ marginRight: '10px' }}>로그인 (초기화면)</Link>
        <Link to="/kiosk" style={{ marginRight: '10px' }}>키오스크</Link>
        <Link to="/pos">POS</Link>
      </nav>
      <main>
        <Routes>
          <Route path="/" element={<LoginPage />} />
          <Route path="/kiosk" element={<KioskPage />} />
          <Route path="/pos" element={<PosPage />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
