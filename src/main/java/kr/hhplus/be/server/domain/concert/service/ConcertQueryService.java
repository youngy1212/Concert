package kr.hhplus.be.server.domain.concert.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertQuery;
import kr.hhplus.be.server.domain.concert.service.dto.ConcertDateInfo;
import kr.hhplus.be.server.domain.concert.service.dto.SeatInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertQueryService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ConcertQuery concertQuery;

    public Concert getConcertById(long concertId) {
        return concertQuery.findById(concertId)
                .orElseThrow(() -> new NoSuchElementException("콘서트를 찾을 수 없습니다."));
    }
    
    public Seat getSeat(long seat){
        return concertQuery.getSeat(seat)
                .orElseThrow(() -> new NoSuchElementException("좌석을 찾을 수 없습니다."));
    }

    public ConcertDateInfo getAllConcertSchedule(final long concertId) {

        String key = "concert:schedule:" + concertId;
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

        ConcertDateInfo cachedSchedules = (ConcertDateInfo) valueOps.get(key);
        if (cachedSchedules != null) {
            return cachedSchedules; // 캐시 히트(Cache Hit)
        }

        List<ConcertSchedule> schedules = concertQuery.findAllByConcertId(concertId);

        if(schedules.isEmpty()){
            throw new CustomException("콘서트의 예약 가능한 날을 찾을 수 없습니다.");
        }

        ConcertDateInfo concertDateInfo = new ConcertDateInfo(schedules);

        valueOps.set(key, concertDateInfo, 7, TimeUnit.DAYS);

        return new ConcertDateInfo(schedules);
    }

    public SeatInfo getConcertSeats(final long concertScheduleId) {

        String key = "concert:seats:" + concertScheduleId;
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

        SeatInfo cachedSeatInfo = (SeatInfo) valueOps.get(key);
        if (cachedSeatInfo != null) {
            return cachedSeatInfo;
        }

        List<Long> seatIds = concertQuery.findByConcertScheduleId(concertScheduleId);

        if(seatIds.isEmpty()){
            throw new NoSuchElementException("콘서트의 좌석을 찾을 수 없습니다.");
        }

        SeatInfo seatInfo = new SeatInfo(seatIds);
        valueOps.set(key, seatInfo, 7, TimeUnit.DAYS);

        return new SeatInfo(seatIds);
    }

    public ConcertSchedule getConcertSchedule(final long concertScheduleId){
        return concertQuery.getConcertSchedule(concertScheduleId).orElseThrow(() -> new NoSuchElementException("콘서트 일정을 찾을 수 없습니다."));
    }



}
