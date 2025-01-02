package kr.hhplus.be.server.api.concert.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ConcertDateResponse {

    private Long concert_id;
    private LocalDateTime concert_date;

    private ConcertDateResponse(Long concert_id, LocalDateTime concert_date) {
        this.concert_id = concert_id;
        this.concert_date = concert_date;
    }

    public static ConcertDateResponse of(Long concert_id, LocalDateTime concert_date) {
        return new ConcertDateResponse(concert_id,concert_date);

    }
}
