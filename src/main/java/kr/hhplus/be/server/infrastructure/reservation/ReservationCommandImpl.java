package kr.hhplus.be.server.infrastructure.reservation;

import java.util.Optional;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.repository.ReservationCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationCommandImpl implements ReservationCommand {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation reservationSave(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findByIdLock(Long reservationId) {
        return reservationJpaRepository.findByIdLock(reservationId);
    }
}
