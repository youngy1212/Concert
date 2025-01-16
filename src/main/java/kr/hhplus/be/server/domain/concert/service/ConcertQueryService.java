package kr.hhplus.be.server.domain.concert.service;

import java.util.List;
import java.util.NoSuchElementException;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertQuery;
import kr.hhplus.be.server.domain.concert.service.dto.ConcertDateDto;
import kr.hhplus.be.server.domain.concert.service.dto.SeatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertQueryService {

    private final ConcertQuery concertQuery;

    public Concert getConcertById(long concertId) {
        return concertQuery.findById(concertId)
                .orElseThrow(() -> new NoSuchElementException("콘서트를 찾을 수 없습니다."));
    }
    
    public Seat getSeat(long seat){
        return concertQuery.getSeat(seat)
                .orElseThrow(() -> new NoSuchElementException("좌석을 찾을 수 없습니다."));
    }

    public ConcertDateDto getAllConcertSchedule(final long concertId) {

        List<ConcertSchedule> schedules = concertQuery.findAllByConcertId(concertId);

        if(schedules.isEmpty()){
            throw new CustomException("콘서트의 예약 가능한 날을 찾을 수 없습니다.");
        }

        return new ConcertDateDto(schedules);
    }

    public SeatDto getConcertSeats(final long concertScheduleId) {
        List<Long> seatIds = concertQuery.findByConcertScheduleId(concertScheduleId);

        if(seatIds.isEmpty()){
            throw new NoSuchElementException("콘서트의 좌석을 찾을 수 없습니다.");
        }

        return new SeatDto(seatIds);
    }

    public ConcertSchedule getConcertSchedule(final long concertScheduleId){
        return concertQuery.getConcertSchedule(concertScheduleId).orElseThrow(() -> new NoSuchElementException("콘서트 일정을 찾을 수 없습니다."));
    }



}
