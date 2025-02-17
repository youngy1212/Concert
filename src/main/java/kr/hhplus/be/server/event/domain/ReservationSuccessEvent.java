package kr.hhplus.be.server.event.domain;

import java.time.LocalDateTime;
import java.util.Map;

public class ReservationSuccessEvent {

    private LocalDateTime occurredAt;  // 이벤트 발생 시간
    private String eventId;            // 이벤트 고유 식별자
    private Map<String, Object> metadata; // 추가 메타데이터
    private Long reservationId;


    public ReservationSuccessEvent(Long id) {
    }

}
