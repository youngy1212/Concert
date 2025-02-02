package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.PaymentReservationInfo;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.service.ConcertQueryService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.payment.service.PaymentCommandService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.service.ReservationCommandService;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.service.UserQueryService;
import kr.hhplus.be.server.infrastructure.gateway.PaySystem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    //좌석 예약
    private final ReservationCommandService reservationCommandService;
    private final ConcertQueryService concertQueryService;
    private final UserQueryService userQueryService;
    private final PaymentCommandService paymentCommandService;

    //예약 및 결제 완료
    @Transactional
    public PaymentReservationInfo completeReservation(Long userId, Long ConcertScheduleId, Long seatId, String tokenId, Long ReservationId, String paymentData) {

        User user = userQueryService.getUserById(userId);
        ConcertSchedule concertSchedule = concertQueryService.getConcertSchedule(ConcertScheduleId);
        Seat seat = concertQueryService.getSeat(seatId);

        Reservation reservation = reservationCommandService.findByLock(ReservationId); //락

        reservation.validateReservation(user, seat);
        PaySystem.pay(seat.getPrice());

        //결제 성공시
        reservation.book();
        Payment payment = paymentCommandService.savePayment(user, reservation, seat.getPrice(), PaymentStatus.SUCCESS);
        return new PaymentReservationInfo(concertSchedule.getId(),user.getId(), seat.getId(), payment.getId(), payment.getAmount());

    }
}
