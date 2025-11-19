## 주문 생성 (키오스크)
POST /api/orders
Body:
{
"tableId": number,
"items": [
{ "menuId": number, "quantity": number }
]
}

Response:
{
"orderId": number,
"tableNumber": number,
"status": "WAITING",
"totalAmount": number,
"orderTime": "yyyy-MM-ddTHH:mm:ss"
}

## 결제 처리 (POS)
POST /api/orders/{orderId}/payments
Body:
{
"method": "CARD" | "CASH",
"paidAmount": number
}

Response:
{
"paymentId": number,
"orderId": number,
"method": string,
"totalAmount": number,
"paidAmount": number,
"change": number,
"paymentTime": string
}
