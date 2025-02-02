package kr.hhplus.be.server.api.concert;

import java.util.List;
import kr.hhplus.be.server.annotation.AuthorizationHeader;
import kr.hhplus.be.server.api.concert.dto.ConcertDateResponse;
import kr.hhplus.be.server.api.concert.dto.SeatResponse;
import kr.hhplus.be.server.api.concert.dto.SwaggerConcertController;
import kr.hhplus.be.server.domain.concert.service.ConcertQueryService;
import kr.hhplus.be.server.domain.concert.service.dto.ConcertDateInfo;
import kr.hhplus.be.server.domain.concert.service.dto.SeatInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConcertController implements SwaggerConcertController {

    private final ConcertQueryService concertQueryService;

    @AuthorizationHeader
    @GetMapping("/concert/date/{concertId}")
    public ResponseEntity<List<ConcertDateResponse>> getConcertDates(
            @PathVariable Long concertId){
        ConcertDateInfo concertSchedule = concertQueryService.getAllConcertSchedule(concertId);
        List<ConcertDateResponse> responseList = concertSchedule.concertSchedules().stream()
                .map(ConcertDateResponse::of)
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @AuthorizationHeader
    @GetMapping("/concert/seats/{concertScheduleId}")
    public ResponseEntity<SeatResponse> getConcertSeats(
            @PathVariable Long concertScheduleId
    ){
        SeatInfo concertSeats = concertQueryService.getConcertSeats(concertScheduleId);
        return ResponseEntity.ok(SeatResponse.of(concertSeats.seatIds()));
    }

}
