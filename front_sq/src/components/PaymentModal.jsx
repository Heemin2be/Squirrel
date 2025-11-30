import React, { useState, useEffect } from 'react';
import apiClient from '../api/axios';
import './PaymentModal.css';

function PaymentModal({ orderId, tableId, totalAmount, onPaymentSuccess, onClose }) {
  const [paymentMethod, setPaymentMethod] = useState(null); // 'card' or 'cash'
  const [cashReceived, setCashReceived] = useState('');
  const [change, setChange] = useState(0);
  const [isProcessing, setIsProcessing] = useState(false);
  const [receiptData, setReceiptData] = useState(null); // New state for receipt data

  useEffect(() => {
    if (paymentMethod === 'cash' && cashReceived) {
      const received = parseInt(cashReceived.replace(/,/g, ''), 10);
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
      const received = parseInt(cashReceived.replace(/,/g, ''), 10);
      if (isNaN(received) || received < totalAmount) {
        alert('받은 금액이 결제할 금액보다 적습니다.');
        return;
      }
      method = 'CASH';
      paidAmount = received;
    } else {
      return; // Should not happen
    }

    const isBulkPayment = tableId != null;
    const url = isBulkPayment ? `/tables/${tableId}/payment` : `/orders/${orderId}/payment`;
    
    const paymentData = {
      method: method,
      amount: paidAmount, // The JSON property is 'amount'
    };

    setIsProcessing(true);
    try {
      const paymentResponse = await apiClient.post(url, paymentData);
      
      if (isBulkPayment) {
        alert('테이블 전체 결제가 완료되었습니다.');
        onPaymentSuccess();
        onClose();
      } else {
        console.log("Received receipt data:", paymentResponse.data);
        setReceiptData(paymentResponse.data);
      }

    } catch (error) {
      console.error('Payment failed:', error);
      alert(`결제 처리 중 오류가 발생했습니다: ${error.response?.data?.message || error.message}`);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleOverlayClick = () => {
    if (receiptData) {
      onPaymentSuccess();
    }
    onClose();
  };

  return (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="modal-content payment-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>결제</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          {receiptData ? ( // If receiptData is available, show receipt
            <div className="receipt-view">
              <h3>영수증</h3>
              <p>주문 번호: {receiptData.orderId}</p>
              {receiptData.tableNumber && <p>테이블 번호: {receiptData.tableNumber}</p>}
              <p>주문 시간: {new Date(receiptData.orderTime).toLocaleString()}</p>
              <p>결제 시간: {new Date(receiptData.paymentTime).toLocaleString()}</p>
              <p>결제 방식: {receiptData.paymentMethod}</p>
              <hr />
              <h4>주문 내역</h4>
              <ul className="receipt-items">
                {(receiptData?.items || []).map((item, index) => (
                  <li key={`${item.menuName}-${index}`} className="receipt-item-detail">
                    <span className="receipt-item-name">{item.menuName} x {item.quantity}</span>
                    <span className="receipt-item-price">{item.totalPrice.toLocaleString()}원</span>
                  </li>
                ))}
              </ul>
              <hr />
              <div className="receipt-summary">
                <p>총 결제 금액: <strong>{receiptData.totalAmount.toLocaleString()}원</strong></p>
                {receiptData.method === 'CASH' && (
                  <>
                    <p>받은 금액: {receiptData.paidAmount.toLocaleString()}원</p>
                    <p>거스름돈: {receiptData.changeAmount.toLocaleString()}원</p>
                  </>
                )}
              </div>
              <button className="action-button primary" onClick={() => {onPaymentSuccess(); onClose();}}>확인</button>
            </div>
          ) : ( // Otherwise, show payment options/details
            <>
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
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default PaymentModal;
