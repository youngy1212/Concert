package kr.hhplus.be.server.infrastructure.reservation;

import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.reservation.repository.ReservationQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReservationQueryImpl implements ReservationQuery {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Optional<Reservation> getReservation(Long reservationId) {
        return reservationJpaRepository.findById(reservationId);
    }

    @Override
    public boolean existingReservation(Long concertScheduleId, Long seatId, List<ReservationStatus> statuses) {
        return reservationJpaRepository.existsByConcertScheduleIdAndSeatIdAndStatusIn(concertScheduleId,seatId,
                statuses);
    }
}
