package kr.hhplus.be.server.infrastructure.spring.reservation.event;

import kr.hhplus.be.server.domain.reservation.event.ReservationSuccessEvent;

public interface ReservationEventPublisher {
    void publish(ReservationSuccessEvent event);
}
