# 📚 다람골 Kiosk & POS API (v1.0)

- **기본 URL** : `http://localhost:8080`
- **인증** : JWT (직원 PIN 로그인 `/api/auth/login` → `Authorization: Bearer <token>`)
- **권한 요약**
  | 구분 | 접근 권한 |
  | --- | --- |
  | `/api/auth/login`, `/ws/**`, `/topic/**`, `/app/**` | 전체 허용 |
  | `/api/menus` `GET`, `/api/categories` `GET`, `/api/tables` `GET`, `/api/orders (POST)` | 전체 허용 (키오스크 용) |
  | `/api/menus/**`, `/api/categories/**`, `/api/employees/**`, `/api/stats/**` | ROLE_ADMIN |
  | `/api/orders/**`, `/api/payments/**`, `/api/attendance/**` | 로그인 필요 |

---

## 🔌 WebSocket

- **엔드포인트** : `/ws`
- **서버 브로커** : `/topic/**`

### 메시지 종류
| type | 설명 | payload |
| --- | --- | --- |
| `menu-update` | 메뉴 생성/수정/품절/삭제 시 모든 클라이언트 동기화 | `{ menuId, name, price, cost, imageUrl, isSoldOut, categoryId, deleted }` (`deleted=true` 는 삭제) |
| `new-order` | 키오스크 주문 생성 시 POS 알림 | `{ orderId, status, tableNumber, orderTime, totalPrice }` |
| `order-status-changed` | 결제 완료 시 POS 알림 | `OrderStatusChangedPayload` (기존 구조 유지) |

---

## 1. 인증 (Authentication)

### POST `/api/auth/login`
- 직원 PIN 로그인 (이름은 선택 입력값, PIN만 검사)
```json
{ "pin": "1234" }
```
```json
{
  "accessToken": "eyJ...",
  "employeeName": "김도훈",
  "role": "ROLE_ADMIN"
}
```

---

## 2. 카테고리 (Category)

| 메서드 | 경로 | 권한 | 설명 |
| --- | --- | --- | --- |
| GET | `/api/categories` | 전체 허용 | 전체 목록 |
| GET | `/api/categories/{id}` | 전체 허용 | 단일 조회 |
| POST | `/api/categories` | ROLE_ADMIN | 생성 |
| PUT | `/api/categories/{id}` | ROLE_ADMIN | 수정 |
| DELETE | `/api/categories/{id}` | ROLE_ADMIN | 삭제 (하위 메뉴 없을 때만) |

요청/응답 예:
```json
// POST /api/categories
{ "name": "사이드" }

// 200/201 응답
{ "id": 3, "name": "사이드" }
```

---

## 3. 메뉴 (Menu)

### GET `/api/menus`
- **설명**: 모든 메뉴 조회
- **쿼리**: `?categoryId=1` (선택)
- **응답**
```json
[
  {
    "id": 1,
    "name": "제육덮밥",
    "price": 9000,
    "cost": 5000,
    "isSoldOut": false,
    "imageUrl": "/images/jeyuk.png",
    "categoryId": 1,
    "categoryName": "메인"
  }
]
```

### GET `/api/menus/available`
- **설명**: 품절이 아닌 메뉴 (선택적으로 `categoryId` 사용 가능)

### GET `/api/menus/{id}`
- 단일 조회

### POST `/api/menus` `[ROLE_ADMIN]`
```json
{
  "name": "비빔밥",
  "price": 8000,
  "cost": 4000,
  "imageUrl": "/images/bibim.png",
  "categoryId": 2
}
```
→ 201 Created + 생성된 메뉴

### PUT `/api/menus/{id}` `[ROLE_ADMIN]`
- 전체 수정 (요청 형식 POST와 동일)

### PATCH `/api/menus/{id}` `[ROLE_ADMIN]`
- 주 사용: 품절 상태 변경
```json
{ "isSoldOut": true }
```

### DELETE `/api/menus/{id}` `[ROLE_ADMIN]`
- 204 No Content

---

## 4. 테이블 (Store Table)

### GET `/api/tables`
```json
[
  { "id": 1, "tableNumber": 1, "status": "EMPTY" },
  { "id": 2, "tableNumber": 2, "status": "OCCUPIED" }
]
```

---

## 5. 주문 (Order)

### POST `/api/orders` *(키오스크)*
```json
{
  "tableId": 1,
  "items": [
    { "menuId": 1, "quantity": 2 },
    { "menuId": 3, "quantity": 1 }
  ]
}
```
```json
{
  "orderId": 101,
  "tableNumber": "1",
  "status": "PENDING",
  "totalPrice": 19000,
  "orderTime": "2025-11-20T20:30:15",
  "items": [
    { "name": "제육덮밥", "quantity": 2, "price": 9000 },
    { "name": "콜라", "quantity": 1, "price": 1000 }
  ]
}
```

### GET `/api/orders` *(POS, 인증 필요)*
- **쿼리**: `status=PENDING|PAID|CANCELED`, `date=yyyy-MM-dd` (선택)
- **응답**
```json
[
  {
    "orderId": 101,
    "tableNumber": "1",
    "status": "PAID",
    "totalPrice": 19000,
    "orderTime": "2025-11-20T20:30:15"
  }
]
```

### GET `/api/orders/{id}`
- 단건 상세 (CreateOrderResponse 형식)

---

## 6. 결제 (Payment)

### POST `/api/orders/{orderId}/payment`
```json
{
  "method": "CASH",   // 또는 CARD
  "amount": 20000
}
```
```json
{
  "paymentId": 7,
  "orderId": 101,
  "method": "CASH",
  "totalAmount": 19000,
  "paidAmount": 20000,
  "change": 1000,
  "paymentTime": "2025-11-20T20:45:10"
}
```
- **규칙**
  - CASH : `amount >= totalAmount`
  - CARD : `amount == totalAmount`
  - PENDING 주문만 결제 가능

### POST `/api/orders/{orderId}/cancel`
- 결제 취소 → 주문 상태 `CANCELED`, 결제 상태 `CANCELED`

### GET `/api/payments?date=yyyy-MM-dd`
- 특정 날짜 결제 내역 (취소 포함)

---

## 7. 직원 (Employee) `[ROLE_ADMIN]`

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| GET | `/api/employees` | 전체 목록 |
| POST | `/api/employees` | 새 직원 (PIN 암호화 저장) |
| PATCH | `/api/employees/{id}/wage` | 시급 변경 |
| DELETE | `/api/employees/{id}` | 삭제 |

POST 예시:
```json
{
  "name": "홍길동",
  "pin": "0000",
  "hourlyWage": 12000,
  "role": "ROLE_ADMIN"
}
```

---

## 8. 근태 (Attendance)

### POST `/api/attendance/clock-in`
### POST `/api/attendance/clock-out`
- 로그인한 직원의 출퇴근 기록

### GET `/api/attendance?employeeId=1`
- **기본**: 본인 기록 반환
- **관리자**: `employeeId` 로 임의 직원 검색 가능
```json
[
  {
    "id": 10,
    "employeeId": 1,
    "employeeName": "홍길동",
    "clockIn": "2025-11-20T09:00:00",
    "clockOut": "2025-11-20T18:00:00"
  }
]
```

---

## 9. 통계 (Statistics) `[ROLE_ADMIN]`

| 경로 | 설명 |
| --- | --- |
| `GET /api/stats/sales/day?date=yyyy-MM-dd` | 특정 일 매출 |
| `GET /api/stats/sales/month?year=2025&month=11` | 월 매출 + 일별 breakdown |
| `GET /api/stats/sales?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd` | 기간 매출 합계 + 일별 breakdown |
| `GET /api/stats/top-menus?startDate=...&endDate=...&limit=5` | 인기 메뉴 (결제 기준) |
| `GET /api/stats/orders-by-hour?date=yyyy-MM-dd` | 시간대별 주문 수 |

---

## ✅ 에러 처리
- 공통 예외 응답
```json
{
  "message": "에러 메시지",
  "status": 400,
  "timestamp": "2025-11-20T20:45:10"
}
```
- 인증 실패(401), 권한 부족(403), 잘못된 요청(400), 리소스 없음(404) 등 상황에 따라 HTTP Status + `GlobalExceptionHandler` 가 메시지를 내려줍니다.
