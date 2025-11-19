import React from 'react';
import './Cart.css';

function Cart({ cart, onUpdateQuantity, onRemoveItem }) {
  const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <aside className="cart-container">
      <h2>장바구니</h2>
      {cart.length === 0 ? (
        <p className="cart-empty-message">장바구니가 비어 있습니다.</p>
      ) : (
        <>
          <ul className="cart-items">
            {cart.map(item => (
              <li key={item.id} className="cart-item">
                <div className="item-info">
                  <span className="item-name">{item.name}</span>
                  <span className="item-price">{item.price.toLocaleString()}원</span>
                </div>
                <div className="item-controls">
                  <button onClick={() => onUpdateQuantity(item.id, item.quantity - 1)}>-</button>
                  <span>{item.quantity}</span>
                  <button onClick={() => onUpdateQuantity(item.id, item.quantity + 1)}>+</button>
                  <button className="remove-button" onClick={() => onRemoveItem(item.id)}>x</button>
                </div>
              </li>
            ))}
          </ul>
          <div className="cart-total">
            <strong>총 금액:</strong>
            <span>{total.toLocaleString()}원</span>
          </div>
        </>
      )}
    </aside>
  );
}

export default Cart;
