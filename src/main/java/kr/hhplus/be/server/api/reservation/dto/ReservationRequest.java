package kr.hhplus.be.server.api.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationRequest {
    private Long userId;
    private Long seatId;
    private String tokenId;
    private Long temporaryReservationId;
    private Long concertScheduleId;
    private String paymentData;

    @Builder
    public ReservationRequest(Long userId, Long seatId, String tokenId, Long temporaryReservationId,
                              Long concertScheduleId,
                              String paymentData) {
        this.userId = userId;
        this.seatId = seatId;
        this.tokenId = tokenId;
        this.temporaryReservationId = temporaryReservationId;
        this.concertScheduleId = concertScheduleId;
        this.paymentData = paymentData;
    }

    public ReservationRequest toServiceRequest() {
        return ReservationRequest.builder()
                .userId(userId)
                .seatId(seatId)
                .tokenId(tokenId)
                .temporaryReservationId(temporaryReservationId)
                .concertScheduleId(concertScheduleId)
                .paymentData(paymentData)
                .build();
    }
}

