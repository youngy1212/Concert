package kr.hhplus.be.server.domain.reservation.repository;

import java.util.Optional;
import kr.hhplus.be.server.domain.reservation.model.Reservation;

public interface ReservationCommand {

    Reservation reservationSave(Reservation reservation);

    Optional<Reservation> findByIdLock(Long reservationId);
}
