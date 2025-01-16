package kr.hhplus.be.server.infrastructure.reservation;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Reservation s WHERE s.id = :reservationId")
    Optional<Reservation> findByIdLock(@Param("reservationId") long reservationId);

    boolean existsByConcertScheduleIdAndSeatIdAndStatusIn(Long concertScheduleId, Long seatId, List<ReservationStatus> statuses);
}
