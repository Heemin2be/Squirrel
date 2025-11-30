import React, { useState, useEffect, useRef } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Client } from "@stomp/stompjs";
import Cart from "../components/Cart";
import apiClient from "../api/axios";
import "./KioskPage.css";

function KioskPage() {
  const { tableId } = useParams();
  const navigate = useNavigate();

  const [currentTable, setCurrentTable] = useState(null);
  const [categories, setCategories] = useState([]);
  const [activeCategory, setActiveCategory] = useState(null);
  const [menus, setMenus] = useState([]);
  const [cart, setCart] = useState([]);
  const [loadingTable, setLoadingTable] = useState(true);
  const [errorTable, setErrorTable] = useState(null);

  const stompClientRef = useRef(null);
  const activeCategoryRef = useRef(activeCategory);

  useEffect(() => {
    activeCategoryRef.current = activeCategory;
  }, [activeCategory]);

  // Helper function to sort menus (available first, then sold-out)
  const sortMenus = (menuArray) => {
    return [...menuArray].sort((a, b) => {
      // Available (false) comes before SoldOut (true)
      if (a.isSoldOut && !b.isSoldOut) return 1; // a is sold out, b is not -> a comes after b
      if (!a.isSoldOut && b.isSoldOut) return -1; // a is not sold out, b is -> a comes before b
      // If same sold-out status, maintain original order or sort by name/id if preferred
      return 0; 
    });
  };

  // Initial data fetching and WebSocket connection
  useEffect(() => {
    const fetchTable = async () => {
      try {
        setLoadingTable(true);
        const response = await apiClient.get(`/tables/${tableId}`);
        setCurrentTable(response.data);
        setErrorTable(null);
      } catch (error) {
        console.error("Error fetching table:", error);
        setErrorTable("테이블 정보를 불러오지 못했습니다.");
      } finally {
        setLoadingTable(false);
      }
    };

    const fetchCategories = async () => {
      try {
        const catResponse = await apiClient.get('/categories');
        setCategories(catResponse.data);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    };
    
    fetchTable();
    fetchCategories();

    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      client.subscribe('/topic/menu-update', message => {
        const updateMsg = JSON.parse(message.body);
        if (updateMsg.type === 'menu-update') {
          const payload = updateMsg.payload;
          
          setMenus(prevMenus => {
            let newMenusState;
            const menuExists = prevMenus.some(m => m.id === payload.menuId);

            if (payload.deleted) {
              newMenusState = prevMenus.filter(m => m.id !== payload.menuId);
            } else if (menuExists) {
              newMenusState = prevMenus.map(m => 
                m.id === payload.menuId 
                  ? { ...m, 
                      name: payload.name, 
                      price: payload.price, 
                      imageUrl: payload.imageUrl, 
                      isSoldOut: payload.isSoldOut
                    } 
                  : m
              );
            } else if (!menuExists && payload.categoryId === activeCategoryRef.current) {
               const newMenu = {
                 id: payload.menuId,
                 name: payload.name,
                 price: payload.price,
                 cost: payload.cost,
                 imageUrl: payload.imageUrl,
                 isSoldOut: payload.isSoldOut,
                 categoryId: payload.categoryId
               };
               newMenusState = [...prevMenus, newMenu];
            } else {
              newMenusState = prevMenus;
            }
            
            return sortMenus(newMenusState);
          });
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
  }, [tableId]);

  useEffect(() => {
    if (categories.length > 0 && !activeCategory) {
      setActiveCategory(categories[0].id);
    }
  }, [categories, activeCategory]);

  useEffect(() => {
    if (activeCategory) {
      const fetchMenus = async () => {
        try {
          const response = await apiClient.get(`/menus?categoryId=${activeCategory}`);
          setMenus(sortMenus(response.data));
        } catch (error) {
          console.error(`Error fetching menus for category ${activeCategory}:`, error);
        }
      };
      fetchMenus();
    }
  }, [activeCategory]);
  
  const handleAddToCart = (menu) => {
    if (menu.isSoldOut) {
      alert("이 메뉴는 현재 품절입니다.");
      return;
    }
    setCart((prevCart) => {
      const existingItem = prevCart.find((item) => item.id === menu.id);
      if (existingItem) {
        return prevCart.map((item) =>
          item.id === menu.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      }
      return [...prevCart, { ...menu, quantity: 1 }];
    });
  };

  const handleUpdateQuantity = (menuId, newQuantity) => {
    if (newQuantity <= 0) {
      handleRemoveItem(menuId);
    } else {
      setCart((prevCart) =>
        prevCart.map((item) =>
          item.id === menuId ? { ...item, quantity: newQuantity } : item
        )
      );
    }
  };

  const handleRemoveItem = (menuId) => {
    setCart((prevCart) => prevCart.filter((item) => item.id !== menuId));
  };

  const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const handleCheckout = async () => {
    if (cart.length === 0) {
      alert('장바구니에 상품을 담아주세요.');
      return;
    }

    if (!currentTable) {
        alert('테이블 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.');
        return;
    }

    const orderData = {
      tableId: currentTable.id,
      items: cart.map(item => ({
        menuId: item.id,
        quantity: item.quantity,
      })),
    };

    try {
      await apiClient.post('/orders', orderData);
      alert(`주문이 성공적으로 완료되었습니다.\n테이블 ${currentTable.tableNumber}로 가져다 드리겠습니다.`);
      navigate('/kiosk', { replace: true });
      setCart([]);
    } catch (error) {
      console.error('Error creating order:', error);
      alert('주문 생성에 실패했습니다. 다시 시도해주세요.');
    }
  };

  if (loadingTable) {
    return <div className="kiosk-container">테이블 정보를 불러오는 중...</div>;
  }

  if (errorTable) {
    return <div className="kiosk-container error">{errorTable}</div>;
  }

  if (!currentTable) {
    return <div className="kiosk-container error">테이블 정보를 찾을 수 없습니다.</div>;
  }

  return (
    <div className="kiosk-container">
      <div className="kiosk-body">
        <aside className="sidebar">
          <h2>카테고리</h2>
          <ul className="category-list">
            {categories.map((cat) => (
              <li key={cat.id}>
                <button
                  className={`category-button ${
                    activeCategory === cat.id ? "active" : ""
                  }`}
                  onClick={() => setActiveCategory(cat.id)}
                >
                  {cat.name}
                </button>
              </li>
            ))}
          </ul>
        </aside>
        <main className="main-content">
          <h2 className="order-type-display">
            테이블 {currentTable.tableNumber}
          </h2>
          <div className="menu-grid">
            {menus.map((menu) => (
              <div 
                key={menu.id} 
                className={`menu-card ${menu.isSoldOut ? 'sold-out' : ''}`}
                onClick={() => handleAddToCart(menu)}
              >
                {menu.isSoldOut && <div className="sold-out-overlay">품절</div>}
                <div className="menu-card-content">
                  <img
                    src={menu.imageUrl || '/assets/placeholder.png'}
                    alt={menu.name}
                  />
                  <h3>{menu.name}</h3>
                  <p>{menu.price.toLocaleString()}원</p>
                </div>
              </div>
            ))}
          </div>
        </main>
        <Cart
          cart={cart}
          onUpdateQuantity={handleUpdateQuantity}
          onRemoveItem={handleRemoveItem}
        />
      </div>
      <footer className="kiosk-footer">
        <div className="order-summary">
          총 주문 금액: {total.toLocaleString()}원
        </div>
        <button className="checkout-button" onClick={handleCheckout}>주문하기</button>
      </footer>
    </div>
  );
}

export default KioskPage;
