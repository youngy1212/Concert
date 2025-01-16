package kr.hhplus.be.server.domain.reservation.service;

import java.util.List;
import java.util.NoSuchElementException;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.reservation.repository.ReservationQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationQueryService {

    private final ReservationQuery reservationQuery;

    public Reservation getReservation(final Long reservationId) {
        return reservationQuery.getReservation(reservationId).orElseThrow(() -> new NoSuchElementException(
                "잘못된 결제 요청입니다."));

    }

    public boolean existingReservation(final Long concertScheduleId, final Long seatId, List<ReservationStatus> statuses) {
        return reservationQuery.existingReservation(concertScheduleId,seatId,statuses );
    }
}
