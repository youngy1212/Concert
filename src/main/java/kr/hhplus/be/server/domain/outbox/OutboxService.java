package kr.hhplus.be.server.domain.outbox;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createOutbox(final String eventType, final String eventKey, final Object payload)
            throws JsonProcessingException {
        final String payloadJson = objectMapper.writeValueAsString(payload);
        final Outbox outbox = Outbox.create(eventType, eventKey, payloadJson);
        outboxRepository.save(outbox);
    }

    @Transactional
    public void publish(final String eventKey) {
        Outbox outbox = outboxRepository.getByEventKey(eventKey);
        outbox.publish();
    }

    @Transactional(readOnly = true)
    public List<Outbox> findInitBefore(LocalDateTime beforeTime) {
        return outboxRepository.findInitBefore(OutboxStatus.INIT, beforeTime);
    }



}
