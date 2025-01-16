package kr.hhplus.be.server.domain.reservation.service;


import java.util.NoSuchElementException;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.repository.ReservationCommand;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationCommandService {

    private final ReservationCommand reservationCommand;


    //예약 생성
    public Reservation createReservation(ConcertSchedule concertSchedule, User user, Seat seat,String queueTokenId) {

        //예약 생성 (10분 뒤 만료)
        Reservation reservation = Reservation.create(concertSchedule,user,seat,queueTokenId);

        return reservationCommand.reservationSave(reservation);
    }

    //결제시 예약 락
    public Reservation findByLock(final Long reservationId) {
        return reservationCommand.findByIdLock(reservationId).orElseThrow(() -> new NoSuchElementException("예약을 찾을 수 없습니다."));
    }


}
