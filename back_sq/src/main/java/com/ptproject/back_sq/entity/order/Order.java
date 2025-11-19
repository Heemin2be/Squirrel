package com.ptproject.back_sq.entity.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name="orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //어떤 테이블에서 주문했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_table_id")
    private StoreTable storeTable;

    @JsonIgnore //엔터티 직접 반환 시 순환/프록시 방지용
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.WAITING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Order(StoreTable storeTable){
        this.storeTable = storeTable;
        this.status = OrderStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item){
        orderItems.add(item);
    }

    public void changeStatus(OrderStatus status){
        this.status = status;
    }
}
