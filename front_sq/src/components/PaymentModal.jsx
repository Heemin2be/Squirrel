import React, { useState, useEffect } from 'react';
import apiClient from '../api/axios';
import './PaymentModal.css';

function PaymentModal({ orderId, totalAmount, onPaymentSuccess, onClose }) {
  const [paymentMethod, setPaymentMethod] = useState(null); // 'card' or 'cash'
  const [cashReceived, setCashReceived] = useState('');
  const [change, setChange] = useState(0);
  const [isProcessing, setIsProcessing] = useState(false);

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

  const handlePayment = async () => {
    if (isProcessing) return;

    let method;
    let paidAmount;

    if (paymentMethod === 'card') {
      method = 'CARD';
      paidAmount = totalAmount;
    } else if (paymentMethod === 'cash') {
      const received = parseInt(cashReceived, 10);
      if (isNaN(received) || received < totalAmount) {
        alert('받은 금액이 결제할 금액보다 적습니다.');
        return;
      }
      method = 'CASH';
      paidAmount = received;
    } else {
      return; // Should not happen
    }

    const paymentData = {
      orderId: orderId,
      method: method,
      amount: paidAmount,
    };

    setIsProcessing(true);
    try {
      await apiClient.post(`/orders/${orderId}/payment`, paymentData);
      alert('결제가 성공적으로 처리되었습니다.');
      onPaymentSuccess(); // This will close the modal and refresh data in PosPage
    } catch (error) {
      console.error('Payment failed:', error);
      alert(`결제 처리 중 오류가 발생했습니다: ${error.response?.data?.message || error.message}`);
    } finally {
      setIsProcessing(false);
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
              <button onClick={() => setPaymentMethod('card')} disabled={isProcessing}>카드</button>
              <button onClick={() => setPaymentMethod('cash')} disabled={isProcessing}>현금</button>
            </div>
          ) : (
            <div className="payment-details">
              <button className="back-button" onClick={() => setPaymentMethod(null)} disabled={isProcessing}>‹ 뒤로</button>
              {paymentMethod === 'card' && (
                <div className="card-payment">
                  <p>카드를 리더기에 삽입해주세요.</p>
                  <button className="action-button primary" onClick={handlePayment} disabled={isProcessing}>
                    {isProcessing ? '처리중...' : '카드 결제 완료'}
                  </button>
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
                      disabled={isProcessing}
                    />
                    <span>원</span>
                  </div>
                  <div className="change-display">
                    <label>거스름돈</label>
                    <span>{change.toLocaleString()}원</span>
                  </div>
                  <button className="action-button primary" onClick={handlePayment} disabled={isProcessing}>
                    {isProcessing ? '처리중...' : '현금 결제 완료'}
                  </button>
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
