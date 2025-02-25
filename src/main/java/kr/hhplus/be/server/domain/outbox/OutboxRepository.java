package kr.hhplus.be.server.domain.outbox;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository {

    Outbox getByEventKey(String eventKey);
    void save(Outbox outbox);
    List<Outbox> findInitBefore(OutboxStatus outboxStatus, LocalDateTime beforeTime);
}
