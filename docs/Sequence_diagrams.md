# Sequence Diagram

# 유저 대기열 토큰 기능

- 서비스를 이용할 토큰을 발급받는 API를 작성합니다.
- 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함합니다.
- 이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능합니다.

> 기본적으로 폴링으로 본인의 대기열을 확인한다고 가정하며, 다른 방안 또한 고려해보고 구현해 볼 수 있습니다.

```mermaid
sequenceDiagram
    participant Client 
    participant API 
    participant QueueManagementService as 대기열 관리 서비스
    participant DB 

    Client ->> API : 토큰 발급 요청
    API  ->> QueueManagementService : 토큰 발급 요청 전달(유저 UUID)
    QueueManagementService ->> DB : 토큰 및 대기열 정보 저장
    DB -->> QueueManagementService : 토큰 저장 완료
    QueueManagementService -->> API : 토큰 반환
    API -->> Client: 토큰 전달

```

# **예약 가능 날짜 / 좌석 API**

- 예약가능한 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성합니다.
- 예약 가능한 날짜 목록을 조회할 수 있습니다.
- 날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있습니다.

> 좌석 정보는 1 ~ 50 까지의 좌석번호로 관리됩니다.
>

```mermaid
sequenceDiagram
    participant Client 
    participant API 
    participant DB 

    Client ->>API : 예약 가능 날짜 및 좌석 정보 조회 요청
    API ->>DB: 예약 가능 날짜 및 좌석 정보 조회
    DB-->>API : 예약 가능 날짜 및 좌석 정보 반환
    API -->>Client : 예약 가능 날짜 및 좌석 정보 응답 
```

# **좌석 예약 요청 API**

- 날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API 를 작성합니다.
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 약 5분간 임시 배정됩니다. ( 시간은 정책에 따라 자율적으로 정의합니다. )
- 만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되어야 하며 다른 사용자는 예약할 수 없어야 한다.

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant DB 
    participant Scheduler

    Client->>API: 좌석 예약 요청 <br/> (날짜, 좌석 정보)
    API->>DB: 좌석 암시 예약 (5분)
    DB-->>API: 예약 성공/실패 응답
    
    alt 좌석 예약 가능
	    API-->>Client: 예약 가능 응답<br/> 결제 프로세스 연결
    else 좌석 예약 불가
	    API-->>Client: 예약 불가 응답<br/> 좌석 재조회 연결
    end
      
		loop 임시 예약 만료 처리
				Scheduler ->> DB: 만료 시간 조회
				DB -->>Scheduler: 만료 시간 응답
        Scheduler->>DB: 만료된 임시 예약 조회 및 해제
    end

```

# **잔액 충전 / 조회 API**

- 결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성합니다.
- 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant DB 
    participant Payment
    
    Note over Client,DB : 포인트 조회

    Client ->> API: 잔액 조회 요청 <br/>(사용자 ID)
    API ->> DB: 잔액 조회
    DB -->> API: 잔액 반환
    API -->> Client: 잔액 반환
    
    Note over Client,Payment : 포인트 충전
    Client ->> API: 잔액 충전 요청<br/>(사용자 ID, 충전 금액)
    API->> Payment: 결제 요청
    Payment ->>API: 결제 완료 응답
    API->>DB: 사용자 잔액 업데이트 + 충전금액 
    API-->>Client : 잔액 충전 완료 응답<br/>(현재 잔액)
```

# 결제 API

- 결제 처리하고 결제 내역을 생성하는 API 를 작성합니다.
- 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료 시킵니다.

```mermaid
sequenceDiagram
    participant Client 
    participant API
    participant DB 
    participant Payment

    Client ->>API: 결제 요청
    API->>DB: 임시 예약 유효성 확인
    DB-->>API: 임시 예약 유효 여부 반환
    alt 임시 예약 유효
        API->>Payment: 결제 요청
        Payment-->>API: 결제 성공
        API->>DB: 결제 내역 생성
        API->>DB: 좌석 소유권 배정(임시 예약 확정)
        API->>DB: 대기열 토큰 만료 처리
        API-->>Client: 예약 성공 응답
    else 결제 실패
        API-->>Client: 결제 실패 응답
    end
    
```
