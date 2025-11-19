package com.ptproject.back_sq.entity.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptproject.back_sq.entity.menu.Menu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orer_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(nullable = false)
    private int quantity;

    //주문 당시 가격 (나중에 메뉴 가격이 바뀌어도 이 값 유지
    @Column(nullable = false)
    private int price;

    public OrderItem(Order order, Menu menu, int quantity, int price){
        this.order = order;
        this.menu = menu;
        this.quantity = quantity;
        this.price = price;
    }
}
