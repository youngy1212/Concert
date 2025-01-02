# API Specs

모든 API 응답은 ApiResponse<T> 클래스를 사용하는 표준 형식을 따릅니다.

응답 구조는 다음과 같습니다:

```json
{
  "code": "int",
  "status": "String",
  "message": "String",
  "data": "T"
}

```

- code: HTTP 상태 코드.
- status: HTTP 상태 이름.
- message: 응답에 대한 메시지.
- data: 반환되는 실제 데이터.

## 1. 대기열 토큰 발급

---

### Description

- 유저의 대기열 토큰을 발급합니다.

### Request

- Method: POST
- URL: `/tokens`
- Headers:
    - `Content-Type`: application/json
- **Request Body:**

  ```json
    {
      "user_uuid": "uuid"
    }
  ```


### Response

- **Response Body:**
  ```json
  {
    "code": 200,
    "status": "OK",
    "message": "OK",
    "data": {
      "token_uuid": "uuid",
      "expires_at": 2024010212020400,
      "is_valid": "ture"
    }
  }
  ```

- **Error Response :**

  사용자가 존재하지 않는 경우

  ```json
  {
    "code": 404,
    "status": "NOT_FOUND",
    "message": "유저를 찾을 수 없습니다.",
    "data": null
  }
  ```


## 2. 예약 가능 날짜 조회 API

---

### Description

- 예약 가능한 날짜를 조회합니다.

### Request

- Method: GET
- URL: `/concert/date/{concertId}`
- Headers:
    - `Content-Type`: application/json
- Path Parameter:
    - concertId : Long

### Response

- **Response Body**:

  ```json
  {
    "code": 200,
    "status": "OK",
    "message": "OK",
    "data": [
      {
        "concert_id": 1,
        "concert_date": "2025010212020400"
      },
      {
        "concert_id": 2,
        "concert_date": "2025010212020400"
      }
    ]
  }
  ```

- **Error Response :**

  예약 가능한 날짜가 없는 경우

  ```json
  {
    "code": 404,
    "status": "NOT_FOUND",
    "message": "콘서트의 예약 가능한 날을 찾을 수 없습니다",
    "data": null
  }
  ```

## 3. 예약 가능 좌석 조회 API

---

### Description

- 예약 가능한 날짜의 좌석을 조회합니다.

### Request

- Method: POST
- URL: `/concert/seats`
- Headers:
    - `Content-Type`: application/json
- Request Body:
  ```json
  {
    "concert_id": 1,
    "token_uuid": "uuid"
  }
  ```

### Response

- **Response Body**:
  ```json
  {
    "code": 200,
    "status": "OK",
    "message": "OK",
    "data": {
      "seat_ids": [1, 2, 3, 4, 5, "..."]
    }
  }
  ```

- **Error Response :**
    - 좌석 데이터가 존재하지 않는 경우

    ```json
    {
      "code": 404,
      "status": "NOT_FOUND",
      "message": "콘서트의 좌석을 찾을 수 없습니다.",
      "data": null
    }
    ```

## 4. 좌석 예약 요청 API

---

### Description

- 콘서트와 좌석을 선택하여 예약합니다.

### Request

- Method: POST
- URL: `/concert/setas/tem/resevation`
- Headers:
    - `Content-Type`: application/json
- Request Body:
  ```json
  {
    "seat_id": 2,
    "token_uuid": "uuid"
  }
  ```

### Response

- **Response Body**:

  ```json
  {
    "code": 200,
    "status": "OK",
    "message": "OK",
    "data": {
      "temp_reservation_id": 1,
      "expires_at": "2025010212020400"
    }
  }
  ```

- **Error Response :**
    - 좌석 데이터가 존재하지 않는 경우
    ```json
    {
      "code": 404,
      "status": "NOT_FOUND",
      "message": "좌석을 찾을 수 없습니다",
      "data": null
    }
    ```

    - 유효하지 않은 토큰 일 경우
    ```json
    {
      "code": 404,
      "status": "NOT_FOUND",
      "message": "토큰이 유효하지 않습니다. 다시 접속하세요",
      "data": null
    }
    ```

    - 이미 선택된 좌석 일 경우
    ```json
    {
      "code": 409,
      "status": "CONFLICT",
      "message": "이미 선택된 좌석입니다.",
      "data": null
    }
    ```

## 5. 잔액 충전  API

---

### Description

- 유저의 POINT를 충전합니다.

### Request

- Method: POST
- URL: `/point/charge`
- Headers:
    - `Content-Type`: application/json
- Request Body:
  ```json
  {
  "user_uuid": "uuid",
  "amount": 10000
  }
  ```


### Response

- **Response Body**:

    ```json
    {
      "code": 200,
      "status": "OK",
      "message": "OK",
      "data": {
        "totalAmount" : 20000
      }
    }
    ```

- **Error Response :**

  사용자가 존재하지 않는 경우
    ```json
    {
      "code": 404,
      "status": "NOT_FOUND",
      "message": "유저를 찾을 수 없습니다.",
      "data": null
    }
    ```

  충전이 유효하지 않은 경우

    ```json
    {
      "code": 400,
      "status": "BAD_REQUEST",
      "message": "잘못 청구되었습니다.",
      "data": null
    }
    ```


## 6. 잔액 조희 API

---

### Description

- 유저의 POINT를 조회합니다.

### Request

- Method: POST
- URL: `/point`
- Headers:
    - `Content-Type`: application/json
- Request Body:
  ```json
  {
    "user_uuid": "uuid"
  }
  ```


### Response

- **Response Body**:

    ```json
    {
      "code": 200,
      "status": "OK",
      "message": "OK",
      "data": {
        "totalAmount" : 20000
      }
    }
    ```

- **Error Response :**

  사용자가 존재하지 않는 경우

    ```json
    {
      "code": 404,
      "status": "NOT_FOUND",
      "message": "유저를 찾을 수 없습니다.",
      "data": null
    }
    ```


## 7. 결제 API

---

### Description

- 예약에 대한 결제를 진행합니다.

### Request

- Method: POST
- URL: `/reservation/payment`
- Headers:
    - `Content-Type`: application/json
- **Request Body**:

  ```json
  {
    "user_uuid": "uuid",
    "seat_id": 2,
    "token_uuid": "uuid"	
  }
  ```


### Response

- **Response Body**:

    ```json
    {
      "code": 200,
      "status": "OK",
      "message": "OK",
      "data": {
    	  "paymenet_id" : "uuid",
    	  "reservation_id" : 1
      }
    }
    ```

- **Error Response :**

  유효하지 않은 유저 일 경우

    ```json
    {
      "code": 404,
      "status": "NOT_FOUND",
      "message": "유저를 찾을 수 없습니다.",
      "data": null
    }
    ```

  결제에 실패했을 경우

    ```json
    {
      "code": 400,
      "status": "BAD_REQUEST",
      "message": "잘못된 결제 요청입니다.",
      "data": null
    }
    ```