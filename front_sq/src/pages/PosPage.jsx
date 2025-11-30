import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../api/axios";
import { Client } from "@stomp/stompjs";
import OrderDetailsModal from "../components/OrderDetailsModal";
import PaymentModal from "../components/PaymentModal";
import "./PosPage.css";

const PosPage = ({ onRequirePin }) => {
  const navigate = useNavigate();
  const [tables, setTables] = useState([]);
  const [ordersByTable, setOrdersByTable] = useState({});
  const [recentOrders, setRecentOrders] = useState([]);
  const [selectedTable, setSelectedTable] = useState(null);
  const [isOrderModalOpen, setIsOrderModalOpen] = useState(false);
  const [isPaymentModalOpen, setIsPaymentModalOpen] = useState(false);
  const [paymentAmount, setPaymentAmount] = useState(0);
  const [orderToPay, setOrderToPay] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const stompClientRef = useRef(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [tablesResponse, pendingOrdersResponse] = await Promise.all([
        apiClient.get('/tables'),
        apiClient.get('/orders?status=PENDING')
      ]);

      const tablesData = tablesResponse.data;
      const pendingOrders = pendingOrdersResponse.data;

      const activeTables = new Set(pendingOrders.map(o => o.tableNumber));
      const updatedTables = tablesData.map(t => ({
        ...t,
        status: activeTables.has(String(t.tableNumber)) ? 'OCCUPIED' : 'EMPTY'
      }));

      setTables(updatedTables);
      setRecentOrders(pendingOrders);

      const ordersGroupedByTable = pendingOrders.reduce((acc, order) => {
        const tableNum = order.tableNumber;
        if (!acc[tableNum]) acc[tableNum] = [];
        acc[tableNum].push(order);
        return acc;
      }, {});
      setOrdersByTable(ordersGroupedByTable);
      setError(null);
    } catch (err) {
      console.error("Error fetching POS data:", err);
      if (err.response && err.response.status === 403) {
        setError("접근 권한이 없습니다. 다시 로그인해주세요.");
        localStorage.removeItem('accessToken');
        onRequirePin('/pos');
      } else {
        setError("데이터를 불러오는데 실패했습니다.");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      onRequirePin('/pos');
      return;
    }

    fetchData();

    // WebSocket Connection
    const client = new Client({
        brokerURL: 'ws://localhost:8080/ws', // Assumes backend is on localhost:8080
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
        client.subscribe('/topic/new-order', message => {
            const newOrderMessage = JSON.parse(message.body);
            if (newOrderMessage.type === 'new-order') {
                const newOrder = newOrderMessage.payload;
                
                // Update state with the new order
                setRecentOrders(prevOrders => [newOrder, ...prevOrders]);
                setOrdersByTable(prev => {
                    const tableNum = newOrder.tableNumber;
                    const updatedOrders = [...(prev[tableNum] || []), newOrder];
                    return { ...prev, [tableNum]: updatedOrders };
                });
                setTables(prevTables => prevTables.map(t => 
                    String(t.tableNumber) === newOrder.tableNumber 
                        ? { ...t, status: 'OCCUPIED' } 
                        : t
                ));
            }
        });
    };

    client.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

    client.activate();
    stompClientRef.current = client;

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
      }
    };
  }, [onRequirePin]);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('employeeName');
    localStorage.removeItem('role');
    alert('로그아웃 되었습니다.');
    navigate('/');
  };

  const handleTableClick = (table) => {
    if (table.status === "OCCUPIED") {
      setSelectedTable(table);
      setIsOrderModalOpen(true);
    } else {
      alert("빈 테이블입니다. 키오스크에서 주문을 넣어주세요.");
    }
  };

  const handleCloseOrderModal = () => {
    setIsOrderModalOpen(false);
    setSelectedTable(null);
  };

  const handleOpenPaymentModal = (orderId, amount) => {
    setOrderToPay(orderId);
    setPaymentAmount(amount);
    setIsPaymentModalOpen(true);
    setIsOrderModalOpen(false);
  };

  const handlePaymentSuccess = () => {
    fetchData(); // Refresh data after payment
    setIsPaymentModalOpen(false);
    setSelectedTable(null);
    setOrderToPay(null);
  };
  
  if (loading && !error) {
    return <div className="pos-container"><h1>데이터를 불러오는 중...</h1></div>;
  }

  if (error) {
    return <div className="pos-container error"><h1>{error}</h1></div>;
  }

  return (
    <div className="pos-container">
      <header className="pos-header">
        <h1>다람골 POS</h1>
        <nav>
          <button onClick={() => onRequirePin('/pos/sales')}>매출 통계</button>
          <button onClick={() => onRequirePin('/pos/menu')}>메뉴 관리</button>
          <button onClick={() => onRequirePin('/pos/employees')}>직원 관리</button>
          <button onClick={handleLogout} className="logout-button">로그아웃</button>
        </nav>
      </header>
      <div className="pos-body">
        <main className="table-view">
          <div className="table-grid">
            {tables.map((table) => (
              <div
                key={table.id}
                className={`table-card ${table.status.toLowerCase()}`}
                onClick={() => handleTableClick(table)}
              >
                <div className="table-number">{table.tableNumber}</div>
                <div className="table-status">
                  {table.status === "EMPTY" ? "비어있음" : "식사중"}
                </div>
              </div>
            ))}
          </div>
        </main>
        <aside className="order-sidebar">
          <h2>최근 주문 (미결제)</h2>
          <ul className="order-list">
            {recentOrders.map((order) => (
              <li key={order.orderId} className="order-item">
                <div className="order-item-header">
                  테이블 {order.tableNumber} ({new Date(order.orderTime).toLocaleTimeString()})
                </div>
                <div className="order-item-body">
                  총 {order.totalPrice.toLocaleString()}원
                </div>
              </li>
            ))}
          </ul>
        </aside>
      </div>
      {isOrderModalOpen && selectedTable && (
        <OrderDetailsModal
          table={selectedTable}
          orders={ordersByTable[selectedTable.tableNumber] || []}
          onClose={handleCloseOrderModal}
          onOpenPaymentModal={handleOpenPaymentModal}
        />
      )}
      {isPaymentModalOpen && (
        <PaymentModal
          orderId={orderToPay}
          totalAmount={paymentAmount}
          onPaymentSuccess={handlePaymentSuccess}
          onClose={() => setIsPaymentModalOpen(false)}
        />
      )}
    </div>
  );
}

export default PosPage;
