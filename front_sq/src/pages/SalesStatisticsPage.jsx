import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axios';
import './ManagementPages.css';

function SalesStatisticsPage() {
  const navigate = useNavigate();
  const [todayStats, setTodayStats] = useState(null);
  const [weeklyStats, setWeeklyStats] = useState(null);
  const [monthlyStats, setMonthlyStats] = useState(null);
  const [topMenus, setTopMenus] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

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

    const fetchStats = async () => {
      try {
        setLoading(true);
        const today = new Date();
        const todayStr = today.toISOString().split('T')[0];
        
        const sevenDaysAgo = new Date(today);
        sevenDaysAgo.setDate(today.getDate() - 6); // inclusive of today
        const sevenDaysAgoStr = sevenDaysAgo.toISOString().split('T')[0];

        const thirtyDaysAgo = new Date(today);
        thirtyDaysAgo.setDate(today.getDate() - 29); // inclusive of today
        const thirtyDaysAgoStr = thirtyDaysAgo.toISOString().split('T')[0];

        const [
          todayResponse,
          weeklyResponse,
          monthlyResponse,
          topMenusResponse
        ] = await Promise.all([
          apiClient.get('/stats/sales', { params: { startDate: todayStr, endDate: todayStr } }),
          apiClient.get('/stats/sales', { params: { startDate: sevenDaysAgoStr, endDate: todayStr } }),
          apiClient.get('/stats/sales', { params: { startDate: thirtyDaysAgoStr, endDate: todayStr } }),
          apiClient.get('/stats/top-menus', { params: { startDate: todayStr, endDate: todayStr, limit: 5 } })
        ]);
        
        setTodayStats(todayResponse.data);
        setWeeklyStats(weeklyResponse.data);
        setMonthlyStats(monthlyResponse.data);
        setTopMenus(topMenusResponse.data);
        setError('');
      } catch (err) {
        console.error("Error fetching stats:", err);
        setError('통계 데이터를 불러오는 데 실패했습니다. 서버 로그를 확인해주세요.');
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('employeeName');
    localStorage.removeItem('role');
    alert('로그아웃 되었습니다.');
    navigate('/');
  };

  if (loading) {
    return <div className="management-container"><h1>로딩 중...</h1></div>;
  }

  if (error) {
    return (
      <div className="management-container">
        <header className="management-header">
          <h1>매출 통계</h1>
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
        <h1>매출 통계</h1>
        <div>
          <button onClick={() => navigate('/pos')}>POS 돌아가기</button>
          <button onClick={handleLogout} className="logout-button">로그아웃</button>
        </div>
      </header>

      {todayStats && weeklyStats && monthlyStats && (
        <>
          <div className="stats-section">
            <h2>금일 현황 ({new Date().toLocaleDateString()})</h2>
            <div className="stats-card-group">
              <div className="stats-card">
                <h3>오늘 총 매출</h3>
                <p>{todayStats.totalSales.toLocaleString()}원</p>
              </div>
              <div className="stats-card">
                <h3>오늘 총 주문 수</h3>
                <p>{todayStats.totalOrders}건</p>
              </div>
            </div>
          </div>

          <div className="stats-section">
            <h2>기간별 요약</h2>
            <div className="stats-card-group">
              <div className="stats-card">
                <h3>주간 매출 (최근 7일)</h3>
                <p>{weeklyStats.totalSales.toLocaleString()}원</p>
              </div>
              <div className="stats-card">
                <h3>월간 매출 (최근 30일)</h3>
                <p>{monthlyStats.totalSales.toLocaleString()}원</p>
              </div>
            </div>
          </div>
          
          <div className="stats-section">
            <h3>메뉴별 판매 순위 (오늘)</h3>
            <ul className="data-list">
              <li className="data-list-header">
                <span>순위</span>
                <span>메뉴 이름</span>
                <span>판매 수량</span>
                <span>총 판매액</span>
              </li>
              {topMenus.map((item, index) => (
                <li key={item.menuId} className="data-list-item">
                  <span>{index + 1}</span>
                  <span>{item.menuName}</span>
                  <span>{item.totalQuantity}개</span>
                  <span>{item.totalSales.toLocaleString()}원</span>
                </li>
              ))}
            </ul>
          </div>
        </>
      )}
    </div>
  );
}

export default SalesStatisticsPage;
