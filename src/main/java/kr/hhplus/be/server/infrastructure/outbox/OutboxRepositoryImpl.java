package kr.hhplus.be.server.infrastructure.outbox;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.OutboxRepository;
import kr.hhplus.be.server.domain.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutBoxJpaRepository outBoxJpaRepository;

    @Override
    public Outbox getByEventKey(String eventKey) {
        return outBoxJpaRepository.findByEventKey(eventKey);
    }

    @Override
    public void save(Outbox outbox) {
        outBoxJpaRepository.save(outbox);
    }

    @Override
    public List<Outbox> findInitBefore(OutboxStatus outboxStatus, LocalDateTime beforeTime) {
        return outBoxJpaRepository.findByStatusAndCreatedAtBefore(outboxStatus,beforeTime);
    }
}
