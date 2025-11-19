// OrderService.java
package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.order.CreateOrderRequest;
import com.ptproject.back_sq.dto.order.CreateOrderResponse;
import com.ptproject.back_sq.entity.menu.Menu;
import com.ptproject.back_sq.entity.order.Order;
import com.ptproject.back_sq.entity.order.OrderItem;
import com.ptproject.back_sq.entity.order.StoreTable;
import com.ptproject.back_sq.repository.MenuRepository;
import com.ptproject.back_sq.repository.OrderRepository;
import com.ptproject.back_sq.repository.StoreTableRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreTableRepository storeTableRepository;
    private final MenuRepository menuRepository;

    public OrderService(OrderRepository orderRepository,
                        StoreTableRepository storeTableRepository,
                        MenuRepository menuRepository) {
        this.orderRepository = orderRepository;
        this.storeTableRepository = storeTableRepository;
        this.menuRepository = menuRepository;
    }

    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        // 1. 테이블 찾거나 생성
        StoreTable table = storeTableRepository
                .findByTableNumber(request.getTableNumber())
                .orElseGet(() -> storeTableRepository.save(new StoreTable(request.getTableNumber())));

        // 2. 주문 생성
        Order order = new Order(table);

        int totalAmount = 0;

        // 3. 주문 아이템 추가
        for (CreateOrderRequest.Item itemReq : request.getItems()) {
            Menu menu = menuRepository.findById(itemReq.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("메뉴 없음: " + itemReq.getMenuId()));

            int price = menu.getPrice();
            int quantity = itemReq.getQuantity();

            OrderItem orderItem = new OrderItem(order, menu, quantity, price);
            order.addItem(orderItem);

            totalAmount += price * quantity;
        }

        // 4. 저장
        Order saved = orderRepository.save(order);

        return new CreateOrderResponse(saved.getId(), totalAmount);
    }
}
