package com.ptproject.back_sq.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.PrePersist;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime paymentTime;

    // 총 결제 금액
    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    // 손님이 실제 지불한 금액
    @Column(name = "paid_amount", nullable = false)
    private int paidAmount;

    // 거스름돈
    @Column(name = "change_amount", nullable = false)
    private int changeAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime canceledAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    public Payment(int totalAmount, int paidAmount, int changeAmount, PaymentMethod method) {
        this.paymentTime = LocalDateTime.now();
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.changeAmount = changeAmount;
        this.method = method;
        this.status = PaymentStatus.COMPLETED;
    }

    @PrePersist
    public void onCreate(){
        if(this.paymentTime == null){
            this.paymentTime = LocalDateTime.now();
        }
    }

    public void setOrder(Order order) {
        if(this.order != null && this.order != order){
            throw new IllegalStateException("Payment는 이미 다른 주문과 연결되어 있습니다.");
        }
        this.order = order;
    }
    public void cancel(){
        if (this.status == PaymentStatus.CANCELED){
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }
        this.status = PaymentStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }
    public PaymentStatus getStatus(){
        return status;
    }

    public int getPaidAmount() {
        return paidAmount;
    }

    public int getChangeAmount() {
        return changeAmount;
    }
}
