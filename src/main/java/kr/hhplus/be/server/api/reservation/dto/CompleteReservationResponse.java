package kr.hhplus.be.server.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CompleteReservationResponse{

    @Schema( description = "콘서트 스케줄 ID")
    private final Long concertScheduleId;

    @Schema( description = "유저 ID")
    private final Long userId;

    @Schema( description = "좌석 ID")
    private final Long seatId;

    @Schema( description = "결제 ID")
    private final Long paymentId;

    @Schema( description = "결제 금액")
    private final Long amount;

    private CompleteReservationResponse(Long concertScheduleId, Long userId, Long seatId, Long paymentId, Long amount) {
        this.concertScheduleId = concertScheduleId;
        this.userId = userId;
        this.seatId = seatId;
        this.paymentId = paymentId;
        this.amount = amount;
    }


    public static CompleteReservationResponse of( Long concertScheduleId, Long userId, Long seatId, Long paymentId, Long amount) {
        return new CompleteReservationResponse(concertScheduleId,userId,seatId,paymentId,amount);
    }
}
