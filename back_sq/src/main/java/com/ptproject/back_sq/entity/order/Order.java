package com.ptproject.back_sq.entity.order;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`order`") // SQL 예약어라 백틱 필요
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime orderTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_table_id", nullable = false)
    private StoreTable storeTable;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    public Order(StoreTable storeTable) {
        this.storeTable = storeTable;
        this.orderTime = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    //결제 완료
    public void completePayment() {
        if (this.status != OrderStatus.PENDING){
            throw new IllegalStateException("결제 가능한 상태가 아닙니다.");
        }
        this.status = OrderStatus.PAID;
    }

    // 주문 취소 (PENDING 상태에서)
    public void cancel(){
        if(this.status != OrderStatus.PENDING){
            throw new IllegalStateException("대기 중인 주문만 취소할 수 있습니다.");
        }
        this.status = OrderStatus.CANCELED;
    }

    public void addPayment(Payment payment) {
        this.payment = payment;
        payment.setOrder(this);
    }

    // 환불 (결제 완료 상태에서)
    public void refund(){
        if(this.status != OrderStatus.PAID){
            throw new IllegalStateException("결제 완료 상태의 주문만 환불할 수 있습니다.");
        }
        this.status = OrderStatus.CANCELED; // Or a new status like REFUNDED
        if (this.storeTable != null) {
            this.storeTable.empty(); // Empty the table on refund
        }
    }

    //총합 계산
    public int calculateTotalAmount() {
        return items.stream()
                .mapToInt(i -> i.getOrderedPrice() * i.getQuantity())
                .sum();
    }
}
