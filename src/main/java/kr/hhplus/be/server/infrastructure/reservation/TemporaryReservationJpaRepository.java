package kr.hhplus.be.server.infrastructure.reservation;

import kr.hhplus.be.server.domain.reservation.model.TemporaryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryReservationJpaRepository extends JpaRepository<TemporaryReservation, Long> {
}
