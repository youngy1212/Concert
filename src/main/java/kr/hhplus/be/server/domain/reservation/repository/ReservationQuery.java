package kr.hhplus.be.server.domain.reservation.repository;

import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;

public interface ReservationQuery {

    Optional<Reservation> getReservation(Long reservationId);

    boolean existingReservation(Long concertScheduleId, Long seatId, List<ReservationStatus> statuses);
}
