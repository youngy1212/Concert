package kr.hhplus.be.server.infrastructure.token;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.model.QueueTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QueueTokenJpaRepository extends JpaRepository<QueueToken, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<QueueToken> findTop10ByStatusOrderByEnqueuedAtAsc(QueueTokenStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM QueueToken q WHERE q.userId = :userId AND q.concertId = :concertId")
    Optional<QueueToken> findByUserAndConcertLock(@Param("userId") Long userId, @Param("concertId") Long concertId);

}
