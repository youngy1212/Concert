package kr.hhplus.be.server.event;

import kr.hhplus.be.server.event.domain.ReservationSuccessEvent;
import kr.hhplus.be.server.infrastructure.external.DataPlatformSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationSpringEventPublisher implements ReservationEventPublisher  {

    private final DataPlatformSender dataPlatformSender;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(ReservationSuccessEvent event) {
        dataPlatformSender.sendReservationData();
    }

}

