package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.PaymentReservationDto;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.service.ConcertCommandService;
import kr.hhplus.be.server.domain.concert.service.ConcertQueryService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.payment.service.PaymentCommandService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.service.ReservationCommandService;
import kr.hhplus.be.server.domain.reservation.service.ReservationQueryService;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.service.UserQueryService;
import kr.hhplus.be.server.infrastructure.gateway.PaySystem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {


    //좌석 예약
    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;
    private final ConcertQueryService concertQueryService;
    private final ConcertCommandService concertCommandService;
    private final UserQueryService userQueryService;
    private final PaymentCommandService paymentCommandService;

    //예약 및 결제 완료
    @Transactional
    public PaymentReservationDto completeReservation(Long userId, Long ConcertScheduleId, Long seatId, String tokenId, Long ReservationId, String paymentData) {

        User user = userQueryService.getUserById(userId);
        Reservation reservation = reservationCommandService.findByLock(ReservationId); //락 걸고 조회
        ConcertSchedule concertSchedule = concertQueryService.getConcertSchedule(ConcertScheduleId);
        Seat seat = concertCommandService.findByIdLock(seatId); //그냥 조회

        if(!seat.getId().equals(reservation.getSeat().getId())){
            throw new CustomException(HttpStatus.CONFLICT, "예약 정보가 일치하지 않습니다.");
        }

        if(!user.getId().equals(reservation.getUser().getId())){
            throw new CustomException(HttpStatus.CONFLICT, "예약 정보가 일치하지 않습니다.");
        }

        boolean pay = PaySystem.pay(seat.getPrice());

        if(!pay){  //결제 실패시
            throw new CustomException(HttpStatus.PAYMENT_REQUIRED, "결제에 실패하였습니다.");
        }

        //결제 성공시
        //seat.book();
        reservation.book();
        Payment payment = paymentCommandService.savePayment(user, reservation, seat.getPrice(), PaymentStatus.SUCCESS);
        return new PaymentReservationDto(concertSchedule.getId(),user.getId(), seat.getId(), payment.getId(), payment.getAmount());

    }
}
