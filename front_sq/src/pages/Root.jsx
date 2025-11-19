import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Root.css';

function Root() {
  const navigate = useNavigate();

  return (
    <div className="root-container">
      <h1>'다람골' 프로젝트</h1>
      <p>시작할 애플리케이션을 선택해주세요.</p>
      <div className="selection-options">
        <button className="selection-button kiosk" onClick={() => navigate('/kiosk')}>
          키오스크
        </button>
        <button className="selection-button pos" onClick={() => navigate('/pos')}>
          POS
        </button>
      </div>
    </div>
  );
}

export default Root;
