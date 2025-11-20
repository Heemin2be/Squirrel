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
    private OrderStatus status = OrderStatus.WAITING;

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
        this.status = OrderStatus.WAITING;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    //결제 완료
    public void completePayment() {
        if (this.status != OrderStatus.WAITING){
            throw new IllegalStateException("결제 가능한 상태가 아닙니다.");
        }
        this.status = OrderStatus.PAID;
    }

    //결제 취소
    public void cancelPayment(){
        if(this.status != OrderStatus.PAID){
            throw new IllegalStateException("결제 취소는 결제 완료 상태에서만 가능합니다.");
        }
        this.status = OrderStatus.CANCELED;
    }


    public void addPayment(Payment payment) {
        this.payment = payment;
        payment.setOrder(this);
    }
}
