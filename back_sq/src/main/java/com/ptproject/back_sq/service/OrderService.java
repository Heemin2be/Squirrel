package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.order.CreateOrderRequest;
import com.ptproject.back_sq.dto.order.CreateOrderResponse;
import com.ptproject.back_sq.dto.order.OrderSummaryResponse;
import com.ptproject.back_sq.dto.websocket.NewOrderPayload;
import com.ptproject.back_sq.dto.websocket.WebSocketMessage;
import com.ptproject.back_sq.entity.menu.Menu;
import com.ptproject.back_sq.entity.order.Order;
import com.ptproject.back_sq.entity.order.OrderItem;
import com.ptproject.back_sq.entity.order.OrderStatus;
import com.ptproject.back_sq.entity.order.StoreTable;
import com.ptproject.back_sq.repository.MenuRepository;
import com.ptproject.back_sq.repository.OrderRepository;
import com.ptproject.back_sq.repository.StoreTableRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final StoreTableRepository storeTableRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate; // ⭐ WebSocket 전송용

    // 👉 주문 생성 (키오스크에서 호출)
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        // 1) 테이블 조회
        StoreTable table = storeTableRepository.findById(request.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("테이블을 찾을 수 없습니다. id=" + request.getTableId()));

        // 2) 주문 엔티티 생성 (status = PENDING, orderTime = now)
        Order order = new Order(table);

        // 3) 주문 항목 추가
        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Menu menu = menuRepository.findById(itemReq.getMenuId())
                    .orElseThrow(() -> new EntityNotFoundException("메뉴를 찾을 수 없습니다. id=" + itemReq.getMenuId()));

            // 🔹 품절 체크
            if (menu.isSoldOut()) {
                throw new IllegalStateException("품절된 메뉴입니다. id=" + menu.getId());
            }

            OrderItem orderItem = new OrderItem(menu, itemReq.getQuantity());
            order.addItem(orderItem);
        }

        // 4) 테이블 상태를 사용 중으로 변경
        table.occupy();
        // 영속 상태라 save 안 해도 flush 시점에 같이 반영됨

        // 5) 주문 저장
        Order saved = orderRepository.save(order);

        // ⭐ WebSocket: 신규 주문 알림 (POS로 브로드캐스트)
        NewOrderPayload payload = NewOrderPayload.from(saved);
        WebSocketMessage<NewOrderPayload> msg =
                new WebSocketMessage<>("new-order", payload);
        messagingTemplate.convertAndSend("/topic/new-order", msg);

        return CreateOrderResponse.from(saved);
    }

    // 👉 POS 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrders(OrderStatus status, LocalDate date) {

        List<Order> orders;

        // status + date
        if (status != null && date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            orders = orderRepository.findByStatusAndOrderTimeBetween(status, start, end);
        }
        // status 만
        else if (status != null) {
            orders = orderRepository.findByStatus(status);
        }
        // date 만
        else if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            orders = orderRepository.findByOrderTimeBetween(start, end);
        }
        // 둘 다 없음 → 전체 (최근 순)
        else {
            orders = orderRepository.findAllByOrderByOrderTimeDesc();
        }

        return orders.stream()
                .map(OrderSummaryResponse::from)
                .toList();
    }

    // 👉 주문 단건 조회 (POS)
    @Transactional(readOnly = true)
    public CreateOrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. id=" + orderId));

        return CreateOrderResponse.from(order);
    }

    // ❌ 결제 로직은 PaymentService로 이사 완료
}
