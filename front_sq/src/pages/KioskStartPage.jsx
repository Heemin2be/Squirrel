import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axios';
import './KioskStartPage.css';

function KioskStartPage() {
  const navigate = useNavigate();
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTables = async () => {
      try {
        setLoading(true);
        const response = await apiClient.get('/tables');
        setTables(response.data);
        setError(null);
      } catch (err) {
        console.error("Error fetching tables:", err);
        setError("테이블 목록을 불러오는데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };
    fetchTables();
  }, []);

  const handleTableSelect = (tableId) => {
    navigate(`/kiosk-order/${tableId}`);
  };

  if (loading) {
    return <div className="kiosk-start-container">테이블 목록을 불러오는 중...</div>;
  }

  if (error) {
    return <div className="kiosk-start-container error">{error}</div>;
  }

  return (
    <div className="kiosk-start-container">
      <h1>테이블을 선택해주세요</h1>
      <div className="table-grid-start">
        {tables.map(table => (
          <button
            key={table.id}
            className={`table-button ${table.status.toLowerCase()}`}
            onClick={() => handleTableSelect(table.id)}
          >
            테이블 {table.tableNumber} <br />
            ({table.status === 'EMPTY' ? '이용 가능' : '사용 중'})
          </button>
        ))}
      </div>
    </div>
  );
}

export default KioskStartPage;
