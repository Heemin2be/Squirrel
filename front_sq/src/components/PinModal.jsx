import React, { useState } from 'react';
import './PinModal.css';

function PinModal({ onPinSubmit, onClose, message = "PIN을 입력해주세요." }) {
  const [pin, setPin] = useState('');
  const [error, setError] = useState('');

  const handlePinChange = (e) => {
    const value = e.target.value;
    if (value.length <= 4 && /^\d*$/.test(value)) { // Allow only 4 digits
      setPin(value);
      setError('');
    }
  };

  const handleSubmit = () => {
    if (pin.length === 4) {
      onPinSubmit(pin);
      setPin(''); // Clear PIN after submission
    } else {
      setError('PIN은 4자리 숫자여야 합니다.');
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSubmit();
    }
  };

  return (
    <div className="pin-modal-overlay" onClick={onClose}>
      <div className="pin-modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="pin-modal-header">
          <h2>인증 필요</h2>
          <button className="pin-close-button" onClick={onClose}>×</button>
        </div>
        <div className="pin-modal-body">
          <p>{message}</p>
          <input
            type="password" // Use password type for PIN input
            maxLength="4"
            value={pin}
            onChange={handlePinChange}
            onKeyPress={handleKeyPress}
            placeholder="4자리 PIN"
            autoFocus
          />
          {error && <p className="pin-error-message">{error}</p>}
        </div>
        <div className="pin-modal-footer">
          <button className="pin-submit-button" onClick={handleSubmit}>확인</button>
        </div>
      </div>
    </div>
  );
}

export default PinModal;
