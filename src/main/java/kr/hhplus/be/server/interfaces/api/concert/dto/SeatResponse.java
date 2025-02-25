package kr.hhplus.be.server.interfaces.api.concert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Getter
public class SeatResponse {

    @Schema(description = "좌석 ID 리스트")
    private List<Long> seatIds;

    private SeatResponse(List<Long> seatIds ) {
       this.seatIds = seatIds;
    }

    public static SeatResponse of(List<Long> seatIds ) {
        return new SeatResponse(seatIds);
    }
}
