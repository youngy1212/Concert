package kr.hhplus.be.server.infrastructure.concert;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.concert.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s.id FROM Seat s where s.concertScheduleId = :concertScheduleId")
    List<Long> findByConcertScheduleId(@Param("concertScheduleId")long concertScheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId")
    Optional<Seat> findByIdLock(@Param("seatId") long seatId);
}
