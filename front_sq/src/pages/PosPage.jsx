import React, { useState } from "react";
import OrderDetailsModal from "../components/OrderDetailsModal";
import PaymentModal from "../components/PaymentModal"; // Import PaymentModal
import "./PosPage.css";

// Placeholder data
const initialTables = [
  { id: 1, number: "1", status: "empty" },
  { id: 2, number: "2", status: "occupied" },
  { id: 3, number: "3", status: "occupied" },
  { id: 4, number: "4", status: "empty" },
  { id: 5, number: "5", status: "empty" },
  { id: 6, number: "6", status: "occupied" },
  { id: 7, number: "7", status: "empty" },
  { id: 8, number: "8", status: "empty" },
];

// More detailed dummy data for orders by table
const initialOrdersByTable = {
  2: [
    {
      orderId: 101,
      time: "10분 전",
      items: [
        { id: 102, name: "임자탕", price: 9000, quantity: 2 },
        { id: 301, name: "콜라", price: 2000, quantity: 1 },
      ],
    },
  ],
  3: [
    {
      orderId: 103,
      time: "2분 전",
      items: [{ id: 105, name: "도토리파전", price: 12000, quantity: 1 }],
    },
  ],
  6: [
    {
      orderId: 102,
      time: "5분 전",
      items: [
        { id: 107, name: "묵보쌈", price: 30000, quantity: 1 },
        { id: 106, name: "묵밥", price: 8000, quantity: 2 },
        { id: 302, name: "사이다", price: 2000, quantity: 2 },
      ],
    },
  ],
};

// Simplified recent orders for the sidebar (will need to be updated based on ordersByTable state)
const initialRecentOrders = [
  { id: 101, table: "2", items: "임자탕 x 2, 콜라 x 1", time: "10분 전" },
  { id: 102, table: "6", items: "묵보쌈 x 1, 묵밥 x 2, ...", time: "5분 전" },
  { id: 103, table: "3", items: "도토리파전 x 1", time: "2분 전" },
];

function PosPage({ onRequirePin }) {
  const [tables, setTables] = useState(initialTables);
  const [ordersByTable, setOrdersByTable] = useState(initialOrdersByTable);
  const [recentOrders, setRecentOrders] = useState(initialRecentOrders);
  const [selectedTable, setSelectedTable] = useState(null);
  const [isOrderModalOpen, setIsOrderModalOpen] = useState(false);
  const [isPaymentModalOpen, setIsPaymentModalOpen] = useState(false);
  const [paymentAmount, setPaymentAmount] = useState(0);

  const handleTableClick = (table) => {
    if (table.status === "occupied") {
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

  const handleOpenPaymentModal = (amount) => {
    setPaymentAmount(amount);
    setIsPaymentModalOpen(true);
    setIsOrderModalOpen(false); // Close order modal when opening payment modal
  };

  const handlePaymentSuccess = () => {
    // Update table status to empty
    setTables(prevTables =>
      prevTables.map(table =>
        table.id === selectedTable.id ? { ...table, status: "empty" } : table
      )
    );
    // Clear orders for this table
    setOrdersByTable(prevOrders => {
      const newOrders = { ...prevOrders };
      delete newOrders[selectedTable.id];
      return newOrders;
    });
    // Optionally update recent orders display
    setRecentOrders(prevRecentOrders =>
        prevRecentOrders.filter(order => order.table !== String(selectedTable.id))
    );
    setIsPaymentModalOpen(false);
    setSelectedTable(null);
  };

  return (
    <div className="pos-container">
      <header className="pos-header">
        <h1>다람골 POS</h1>
        <nav>
          <button onClick={() => onRequirePin('/pos/sales')}>매출 통계</button>
          <button onClick={() => onRequirePin('/pos/menu')}>메뉴 관리</button>
          <button onClick={() => onRequirePin('/pos/employees')}>직원 관리</button>
        </nav>
      </header>
      <div className="pos-body">
        <main className="table-view">
          <div className="table-grid">
            {tables.map((table) => (
              <div
                key={table.id}
                className={`table-card ${table.status}`}
                onClick={() => handleTableClick(table)}
              >
                <div className="table-number">{table.number}</div>
                <div className="table-status">
                  {table.status === "empty" ? "비어있음" : "식사중"}
                </div>
              </div>
            ))}
          </div>
        </main>
        <aside className="order-sidebar">
          <h2>최근 주문</h2>
          <ul className="order-list">
            {recentOrders.map((order) => (
              <li key={order.id} className="order-item">
                <div className="order-item-header">
                  테이블 {order.table} ({order.time})
                </div>
                <div className="order-item-body">{order.items}</div>
              </li>
            ))}
          </ul>
        </aside>
      </div>
      {isOrderModalOpen && selectedTable && (
        <OrderDetailsModal
          table={selectedTable}
          orders={ordersByTable[selectedTable.id] || []}
          onClose={handleCloseOrderModal}
          onOpenPaymentModal={handleOpenPaymentModal}
        />
      )}
      {isPaymentModalOpen && (
        <PaymentModal
          totalAmount={paymentAmount}
          onPaymentSuccess={handlePaymentSuccess}
          onClose={() => setIsPaymentModalOpen(false)}
        />
      )}
    </div>
  );
}

export default PosPage;
