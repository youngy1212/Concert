package kr.hhplus.be.server.api.reservation;

import kr.hhplus.be.server.annotation.AuthorizationHeader;
import kr.hhplus.be.server.api.reservation.dto.CompleteReservationResponse;
import kr.hhplus.be.server.api.reservation.dto.PaymentReservationRequest;
import kr.hhplus.be.server.api.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.api.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.api.reservation.dto.SwaggerReservationController;
import kr.hhplus.be.server.application.PaymentFacade;
import kr.hhplus.be.server.application.ReservationFacade;
import kr.hhplus.be.server.application.dto.PaymentReservationDto;
import kr.hhplus.be.server.application.dto.ReservationDto;
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
        ReservationDto reservationDto = reservationFacade.reserveSeat(
                request.getUserId(), request.getSeatId(), request.getConcertScheduleId(), request.getTokenId());
        return ResponseEntity.ok(ReservationResponse.of(reservationDto.reservationId(),reservationDto.seatId()));
    }

    @AuthorizationHeader
    @PostMapping("/reservation/payment")
    public ResponseEntity<CompleteReservationResponse> reservationSeat(@RequestBody PaymentReservationRequest request){
        PaymentReservationDto paymentReservationDto = paymentFacade.completeReservation(request.getUserId(),
                request.getConcertScheduleId(), request.getSeatId()
                , request.getTokenId(), request.getReservationId(), request.getPaymentData());
        return ResponseEntity.ok(CompleteReservationResponse.of(paymentReservationDto.concertScheduleId(), paymentReservationDto.userId(),
                paymentReservationDto.seatId(), paymentReservationDto.paymentId(), paymentReservationDto.amount()));
    }

}
