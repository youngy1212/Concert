package kr.hhplus.be.server.api.concert.dto;

import java.util.List;

public class SeatResponse {

    private List<Long> seat_ids;

    private SeatResponse(List<Long> seat_ids) {
       this.seat_ids = seat_ids;
    }

    public static SeatResponse of(List<Long> seat_ids) {
        return new SeatResponse(seat_ids);

    }
}
