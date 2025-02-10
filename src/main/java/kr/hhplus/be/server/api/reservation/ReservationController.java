package kr.hhplus.be.server.api.reservation;

import kr.hhplus.be.server.annotation.AuthorizationHeader;
import kr.hhplus.be.server.api.reservation.dto.CompleteReservationResponse;
import kr.hhplus.be.server.api.reservation.dto.PaymentReservationRequest;
import kr.hhplus.be.server.api.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.api.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.api.reservation.dto.SwaggerReservationController;
import kr.hhplus.be.server.application.PaymentFacade;
import kr.hhplus.be.server.application.ReservationFacade;
import kr.hhplus.be.server.application.dto.PaymentReservationInfo;
import kr.hhplus.be.server.application.dto.ReservationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController implements SwaggerReservationController {

    private final ReservationFacade reservationFacade;
    private final PaymentFacade paymentFacade;

    @AuthorizationHeader
    @PostMapping("/concert/seats/reservation")
    public ResponseEntity<ReservationResponse> reservationSeat(
            @RequestBody ReservationRequest request
    ){
        ReservationInfo reservationInfo = reservationFacade.reserveSeat(
                request.getUserId(), request.getSeatId(), request.getConcertScheduleId());
        return ResponseEntity.ok(ReservationResponse.of(reservationInfo.reservationId(), reservationInfo.seatId()));
    }

    @AuthorizationHeader
    @PostMapping("/reservation/payment")
    public ResponseEntity<CompleteReservationResponse> reservationSeat(@RequestBody PaymentReservationRequest request){
        PaymentReservationInfo paymentReservationInfo = paymentFacade.completeReservation(request.getUserId(),
                request.getConcertScheduleId(), request.getSeatId()
                , request.getReservationId(), request.getPaymentData());
        return ResponseEntity.ok(CompleteReservationResponse.of(paymentReservationInfo.concertScheduleId(), paymentReservationInfo.userId(),
                paymentReservationInfo.seatId(), paymentReservationInfo.paymentId(), paymentReservationInfo.amount()));
    }

}
