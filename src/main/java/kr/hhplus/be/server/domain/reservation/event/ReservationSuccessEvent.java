package kr.hhplus.be.server.domain.reservation.event;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReservationSuccessEvent {

    private final LocalDateTime occurredAt;  // 이벤트 발생 시간
    private final String eventId;            // 이벤트 고유 식별자
    private final Long reservationId;
    private Map<String, Object> metadata; // 추가 메타데이터 (뭘써야할까..)

    public ReservationSuccessEvent(Long reservationId) {
        this.occurredAt = LocalDateTime.now();
        this.eventId = UUID.randomUUID().toString();
        this.reservationId = reservationId;
    }


}
