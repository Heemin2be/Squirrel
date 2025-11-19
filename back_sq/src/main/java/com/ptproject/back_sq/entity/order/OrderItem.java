package com.ptproject.back_sq.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ptproject.back_sq.entity.menu.Menu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private int quantity;

    // 🔹 주문 당시 판매가
    @Column(name = "ordered_price", nullable = false)
    private int orderedPrice;

    // 🔹 주문 당시 원가
    @Column(name = "ordered_cost", nullable = false)
    private int orderedCost;

    public OrderItem(Menu menu, int quantity) {
        this.menu = menu;
        this.quantity = quantity;
        this.orderedPrice = menu.getPrice(); // int
        this.orderedCost = menu.getCost();   // int
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
