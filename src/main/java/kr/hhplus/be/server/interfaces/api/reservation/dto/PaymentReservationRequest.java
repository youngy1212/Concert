package kr.hhplus.be.server.interfaces.api.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentReservationRequest {
    private Long userId;
    private Long seatId;
    private String tokenId;
    private Long reservationId;
    private Long concertScheduleId;
    private String paymentData;

    @Builder
    public PaymentReservationRequest(Long userId, Long seatId, String tokenId, Long reservationId,
                                     Long concertScheduleId,
                                     String paymentData) {
        this.userId = userId;
        this.seatId = seatId;
        this.tokenId = tokenId;
        this.reservationId = reservationId;
        this.concertScheduleId = concertScheduleId;
        this.paymentData = paymentData;
    }

    public PaymentReservationRequest toServiceRequest() {
        return PaymentReservationRequest.builder()
                .userId(userId)
                .seatId(seatId)
                .tokenId(tokenId)
                .reservationId(reservationId)
                .concertScheduleId(concertScheduleId)
                .paymentData(paymentData)
                .build();
    }
}

