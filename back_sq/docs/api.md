# 📚 다람골 Kiosk & POS API 요약

## 🔑 인증

### 로그인 (PIN 기반)

- **POST** `/api/auth/login`
- **요청**
  ```json
  {
    "name": "홍길동",
    "pin": "0000"
  }


## 👤 직원 관리 (관리자 전용)
### 직원 목록 조회

- GET /api/admin/employees
- 권한: ADMIN
- 응답 예시
```
[
{
"id": 1,
"name": "홍길동",
"hourlyWage": 12000,
"role": "ROLE_ADMIN"
}
]
```
### 직원 등록
- POST /api/admin/employees
- 요청
```
{
"name": "홍길동",
"pin": "0000",
"hourlyWage": 12000,
"role": "ROLE_ADMIN"
}
```
### 직원 시급 수정
- PATCH /api/admin/employees/{id}/wage
- 요청
```
{
"hourlyWage": 13000
}
```
### 직원 삭제
- DELETE /api/admin/employees/{id}

## 🍽 메뉴 조회 (공통 / 키오스크용)
### 전체 메뉴 조회 (POS & 테스트용)
- GET /api/menus
### 품절 아닌 메뉴만 조회 (키오스크용)
- GET /api/menus/available
### 단일 메뉴 조회
- GET /api/menus/{id}

## 🍱 메뉴 관리 (관리자 전용)
### 메뉴 목록 조회 (관리용)
- GET /api/admin/menus
### 메뉴 생성
- POST /api/admin/menus
- 요청
```
{
"name": "제육덮밥",
"price": 9000,
"cost": 5000,
"imageUrl": "/images/jeyuk.png",
"categoryId": 1
}
```
### 메뉴 수정
- PUT /api/admin/menus/{id}
- 요청: 위와 동일한 MenuRequest
### 품절 상태 변경
- PATCH /api/admin/menus/{id}/sold-out
- 요청
```
{
"soldOut": true
}
```
### 메뉴 삭제
- DELETE /api/admin/menus/{id}

## 🧾 주문 (키오스크)
### 주문 생성

- POST /api/orders
- 요청
```
{
"tableId": 1,
"items": [
{ "menuId": 1, "quantity": 2 },
{ "menuId": 3, "quantity": 1 }
]
}
```
- 응답
```
{
"orderId": 10,
"tableNumber": 1,
"status": "WAITING",
"totalAmount": 19000,
"orderTime": "2025-11-20T20:30:15"
}
```
## 💳 주문 조회 & 결제 (POS)
### 주문 목록 조회
- GET /api/orders
- 쿼리 파라미터
  - status (선택) : WAITING, PAID, CANCELED
  - date (선택) : yyyy-MM-dd
- 예시
  - /api/orders?status=WAITING
  - /api/orders?date=2025-11-20
  - /api/orders?status=PAID&date=2025-11-20
### 주문 단건 조회
- GET /api/orders/{id}

### 결제 처리
- POST /api/orders/{orderId}/payment
- 요청
```
{
"paidAmount": 20000,
"method": "CASH"   // 또는 "CARD"
}
```
- 응답
```
{
"paymentId": 7,
"orderId": 10,
"method": "CASH",
"totalAmount": 19000,
"paidAmount": 20000,
"change": 1000,
"paymentTime": "2025-11-20T20:45:10"
}
```
- 비즈니스 규칙
  - CASH : paidAmount >= totalAmount 여야 함.
  - CARD : paidAmount == totalAmount 여야 함.
  - CANCELED 주문은 결제 불가.
  - PAID 주문은 재결제 불가.


