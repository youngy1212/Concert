package kr.hhplus.be.server.infrastructure.outbox;

import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OutBoxJpaRepository extends JpaRepository<Outbox, Long> {

    Outbox findByEventKey(String eventKey);

    @Query("SELECT o FROM Outbox o WHERE o.outboxStatus = :status AND o.createdDate < :beforeTime")
    List<Outbox> findByStatusAndCreatedAtBefore(@Param("status") OutboxStatus status, @Param("beforeTime") LocalDateTime beforeTime);
}
