package kr.hhplus.be.server.api.concert;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.api.ApiResponse;
import kr.hhplus.be.server.api.concert.dto.ConcertDateResponse;
import kr.hhplus.be.server.api.concert.dto.SeatRequest;
import kr.hhplus.be.server.api.concert.dto.SeatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConcertController {

    @GetMapping("/concert/date/{concertId}")
    public ApiResponse<List<ConcertDateResponse>> getConcertDates(@PathVariable Long concertId){
        return ApiResponse.ok(List.of(ConcertDateResponse.of(1L, LocalDateTime.now()),
                ConcertDateResponse.of(2L, LocalDateTime.now())));
    }

    @PostMapping("/concert/seats")
    public ApiResponse<SeatResponse> getConcertSeats(@RequestBody SeatRequest request){
        return ApiResponse.ok(SeatResponse.of(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));
    }

}
