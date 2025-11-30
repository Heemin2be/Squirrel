import React from 'react';
import './TableSelectionModal.css';

function TableSelectionModal({ tables, onSelect, onClose }) {
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>테이블을 선택해주세요</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          <div className="table-grid-modal">
            {tables.map(table => (
              <div
                key={table.id}
                className={`table-card-modal ${table.status.toLowerCase()}`}
                onClick={() => onSelect(table)}
              >
                <div className="table-number-modal">{table.tableNumber}</div>
                <div className="table-status-modal">
                  {table.status === 'EMPTY' ? '이용 가능' : '식사중'}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default TableSelectionModal;
