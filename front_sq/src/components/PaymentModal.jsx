import React, { useState, useEffect } from 'react';
import './PaymentModal.css';

function PaymentModal({ totalAmount, onPaymentSuccess, onClose }) {
  const [paymentMethod, setPaymentMethod] = useState(null); // 'card' or 'cash'
  const [cashReceived, setCashReceived] = useState('');
  const [change, setChange] = useState(0);

  useEffect(() => {
    if (paymentMethod === 'cash' && cashReceived) {
      const received = parseInt(cashReceived, 10);
      if (!isNaN(received) && received >= totalAmount) {
        setChange(received - totalAmount);
      } else {
        setChange(0);
      }
    }
  }, [cashReceived, totalAmount, paymentMethod]);

  const handleCashReceivedChange = (e) => {
    const value = e.target.value.replace(/,/g, ''); // Remove commas for calculation
    if (/^\d*$/.test(value)) {
      setCashReceived(value);
    }
  };

  const handlePayment = () => {
    if (paymentMethod === 'card') {
      onPaymentSuccess();
    } else if (paymentMethod === 'cash') {
      const received = parseInt(cashReceived, 10);
      if (isNaN(received) || received < totalAmount) {
        alert('받은 금액이 결제할 금액보다 적습니다.');
        return;
      }
      onPaymentSuccess();
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content payment-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>결제</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          <div className="total-amount-display">
            <span>결제할 금액</span>
            <strong>{totalAmount.toLocaleString()}원</strong>
          </div>

          {!paymentMethod ? (
            <div className="method-selection">
              <button onClick={() => setPaymentMethod('card')}>카드</button>
              <button onClick={() => setPaymentMethod('cash')}>현금</button>
            </div>
          ) : (
            <div className="payment-details">
              <button className="back-button" onClick={() => setPaymentMethod(null)}>‹ 뒤로</button>
              {paymentMethod === 'card' && (
                <div className="card-payment">
                  <p>카드를 리더기에 삽입해주세요.</p>
                  <button className="action-button primary" onClick={handlePayment}>카드 결제 완료</button>
                </div>
              )}
              {paymentMethod === 'cash' && (
                <div className="cash-payment">
                  <div className="cash-input">
                    <label>받은 금액</label>
                    <input
                      type="text"
                      value={cashReceived.toLocaleString()}
                      onChange={handleCashReceivedChange}
                      placeholder="받은 금액 입력"
                      autoFocus
                    />
                    <span>원</span>
                  </div>
                  <div className="change-display">
                    <label>거스름돈</label>
                    <span>{change.toLocaleString()}원</span>
                  </div>
                  <button className="action-button primary" onClick={handlePayment}>현금 결제 완료</button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default PaymentModal;
