package kr.hhplus.be.server.domain.concert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.repository.ConcertQuery;
import kr.hhplus.be.server.domain.concert.service.dto.ConcertDateInfo;
import kr.hhplus.be.server.domain.concert.service.dto.SeatInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Testcontainers
class ConcertRedisServiceTest {

    @Autowired
    private ConcertQueryService concertQueryService;

    @MockitoBean
    private ConcertQuery concertQuery;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    public void flushRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @DisplayName("콘서트 스케줄 조회시 캐시 미스")
    @Test
    void getAllConcertSchedule_CacheMiss() {
        // given
        long concertId = 1L;
        Concert concert = Concert.create("콘서트", "고척돔");
        String key = "concert:schedule:" + concertId;
        ConcertSchedule concertSchedule = ConcertSchedule.create(concertId, LocalDateTime.of(2024, 12, 12, 8, 50));
        ConcertSchedule concertSchedule2 = ConcertSchedule.create(concertId,LocalDateTime.of(2024,12,13,8,50));
        List<ConcertSchedule> schedules =List.of(concertSchedule, concertSchedule2);
        when(concertQuery.findAllByConcertId(concertId)).thenReturn(schedules);
        redisTemplate.delete(key);

        // when
        concertQueryService.getAllConcertSchedule(concertId);

        // then
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        ConcertDateInfo cachedResult = (ConcertDateInfo) valueOps.get(key);
        assertNotNull(cachedResult);
        assertEquals(2, cachedResult.concertSchedules().size());

        //호출 검증
        verify(concertQuery, times(1)).findAllByConcertId(concertId);

    }

    @DisplayName("콘서트 스케줄 조회시 캐시 히트")
    @Test
    void getAllConcertSchedule_CacheHit() {
        // given

        long concertId = 1L;
        Concert concert = Concert.create("콘서트", "고척돔");
        String key = "concert:schedule:" + concertId;
        ConcertSchedule concertSchedule = ConcertSchedule.create(concertId, LocalDateTime.of(2024, 12, 12, 8, 50));
        ConcertSchedule concertSchedule2 = ConcertSchedule.create(concertId,LocalDateTime.of(2024,12,13,8,50));

        List<ConcertSchedule> schedules = List.of(concertSchedule,concertSchedule2);
        ConcertDateInfo concertDateInfo = new ConcertDateInfo(schedules);
        redisTemplate.opsForValue().set(key, concertDateInfo);

        // when
        concertQueryService.getAllConcertSchedule(concertId);

        // then
        verify(concertQuery, times(0)).findAllByConcertId(anyLong());

    }

    @DisplayName("콘서트 좌석 조회시 캐시 미스")
    @Test
    void getSeat_CacheMiss() {
        // given
        long concertScheduleId = 1L;
        String key = "concert:seats:" + concertScheduleId;
        List<Long> seatIds = List.of(1L, 2L);

        when(concertQuery.findByConcertScheduleId(concertScheduleId)).thenReturn(seatIds);
        redisTemplate.delete(key);

        // when
        concertQueryService.getConcertSeats(concertScheduleId);

        // then
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        SeatInfo cachedResult = (SeatInfo) valueOps.get(key);
        assertNotNull(cachedResult);
        assertEquals(2, cachedResult.seatIds().size());

        // Mock 메서드 호출 검증
        verify(concertQuery, times(1)).findByConcertScheduleId(concertScheduleId);

    }

    @DisplayName("콘서트 좌석 조회시 좌석 캐시 히트")
    @Test
    void getSeat_CacheHit() {
        // given
        long concertScheduleId = 1L;
        String key = "concert:seats:" + concertScheduleId;
        List<Long> seatIds = List.of(1L, 2L);
        SeatInfo seatInfo = new SeatInfo(seatIds);
        redisTemplate.opsForValue().set(key, seatInfo);

        // when
        concertQueryService.getConcertSeats(concertScheduleId);

        // then
        verify(concertQuery, times(0)).findAllByConcertId(anyLong());
    }


}