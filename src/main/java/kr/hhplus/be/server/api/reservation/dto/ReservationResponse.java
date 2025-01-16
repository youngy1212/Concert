package kr.hhplus.be.server.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReservationResponse {

    @Schema(description = "예약 ID")
    private Long reservationId;

    @Schema(description = "좌석 ID")
    private Long seatId;

    private ReservationResponse(Long reservationId, Long seatId) {
        this.reservationId = reservationId;
        this.seatId = seatId;
    }

    public static ReservationResponse of(Long reservationId, Long seatId) {
        return new ReservationResponse(reservationId, seatId);
    }
}
