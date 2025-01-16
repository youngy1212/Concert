package kr.hhplus.be.server.api.concert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import lombok.Builder;
import lombok.Getter;


@Getter
public class ConcertDateResponse {

    @Schema( description = "콘서트 ID")
    private final Long concertScheduleId;

    @Schema(description = "콘서트 날짜")
    private final LocalDateTime concertDate;

    @Builder
    private ConcertDateResponse(Long concertScheduleId, LocalDateTime concertDate) {
        this.concertScheduleId = concertScheduleId;
        this.concertDate = concertDate;
    }

    public static ConcertDateResponse of(ConcertSchedule concertSchedule) {
        return ConcertDateResponse.builder()
                .concertScheduleId(concertSchedule.getId())
                .concertDate(concertSchedule.getConcertDate()).build();

    }
}
