package kr.hhplus.be.server.infrastructure.spring.reservation.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.outbox.OutboxService;
import kr.hhplus.be.server.domain.reservation.event.ReservationSuccessEvent;
import kr.hhplus.be.server.infrastructure.kafka.ReservationKafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationSpringEventPublisher implements ReservationEventPublisher {

    private final OutboxService outboxService;
    private final ReservationKafkaEventPublisher reservationKafkaEventPublisher;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(final ReservationSuccessEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(final ReservationSuccessEvent event) throws JsonProcessingException {
        outboxService.createOutbox(event.getClass().getTypeName(), event.getEventId(), event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(final ReservationSuccessEvent event) throws JsonProcessingException {
        String eventJson = objectMapper.writeValueAsString(event);
        reservationKafkaEventPublisher.sendMessage("concert-reserved",event.getEventId(), eventJson);
    }

}

