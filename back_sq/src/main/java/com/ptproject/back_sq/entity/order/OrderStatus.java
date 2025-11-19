package com.ptproject.back_sq.entity.order;

public enum OrderStatus {
    WAITING,     //주문 접수 (결제 전)
    COOKING,     //조리중
    PAID,        //결제 완료
    CANCELED     //취소
}
