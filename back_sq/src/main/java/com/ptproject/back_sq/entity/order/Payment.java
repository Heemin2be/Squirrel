package com.ptproject.back_sq.entity.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "payment")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Column(nullable = false)
    private  int amount;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    public Payment(Order order, PaymentMethod method, int amount){
        this.order = order;
        this.method = method;
        this.amount = amount;
        this.paidAt = LocalDateTime.now();
    }
}
