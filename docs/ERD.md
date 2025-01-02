# ERD 설계

| Table                  | Summay  | Description                               
------------------------|---------|-------------------------------------------
 users                  | 유저 목록   | 보안을 위해 테이블 PK와 엔티티 ID를 함께 사용              
 point                  | 유저의 포인트 |  
 concerts               | 콘서트 정보  |  
 seats                  | 좌석 목록   | status를 통해 예약 가능/예약됨을 구분                  
 temporary_reservations | 임시 예약   | expires_at을 통해 만료 판단 status 만료/예약 판단      
 reservation            | 예약      | 완료된 예약                                    
 queue_tokens           | 토큰 정보   | expires_at을 통해 만료 판단 is_valid 토큰 유효여부를 판단 
 payments               | 결제 내역   | status를 통해 결제 성공/실패 기록                    |


![image](https://github.com/user-attachments/assets/c888dbec-fd54-496c-a1d2-c6030c933daf)