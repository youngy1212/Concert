package kr.hhplus.be.server.event;

import kr.hhplus.be.server.event.domain.ReservationSuccessEvent;

public interface ReservationEventPublisher {
    void send(ReservationSuccessEvent event);
}
