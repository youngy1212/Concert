package kr.hhplus.be.server.domain.concert;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.repository.ConcertQuery;
import kr.hhplus.be.server.domain.concert.service.ConcertQueryService;
import kr.hhplus.be.server.domain.concert.service.dto.ConcertDateInfo;
import kr.hhplus.be.server.domain.concert.service.dto.SeatInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class ConcertQueryServiceTest {

    @Mock
    ConcertQuery concertQuery;

    @InjectMocks
    ConcertQueryService concertQueryService;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOps;

    @DisplayName("공연 정보가 없어서 오류 발생")
    @Test
    void concertNotFound() {
        // given
        long concertId = 1L;

        //  when && then
        when(concertQuery.findById(concertId)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> concertQueryService.getConcertById(concertId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("콘서트를 찾을 수 없습니다.");

    }

    @DisplayName("콘서트 스케줄을 찾을 수 없습니다.")
    @Test
    void ConcertScheduleNotFound() {
        // given
        long concertId = 1L;
        String key = "concert:schedule:" + concertId;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(key)).thenReturn(null);
        when(concertQuery.findAllByConcertId(concertId)).thenReturn(Collections.emptyList());

        // when // then
        assertThatThrownBy(()-> concertQueryService.getAllConcertSchedule(concertId))
                .isInstanceOf(CustomException.class)
                .hasMessage("콘서트의 예약 가능한 날을 찾을 수 없습니다.");
    }

    @DisplayName("콘서트 스케줄을 찾아서 ConcertDateDto 반환")
    @Test
    void ConcertScheduleSuccess() {
        // given
        long concertId = 1L;
        String key = "concert:schedule:" + concertId;

        Concert concert = Concert.create("콘서트", "고척돔");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2024, 12, 12, 8, 50));
        ConcertSchedule concertSchedule2 = ConcertSchedule.create(concert,LocalDateTime.of(2024,12,13,8,50));

        List<ConcertSchedule> schedules = List.of(concertSchedule, concertSchedule2);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(key)).thenReturn(null);
        when(concertQuery.findAllByConcertId(concertId)).thenReturn(schedules);

        // when
        ConcertDateInfo concertDateInfo = concertQueryService.getAllConcertSchedule(concertId);

        // then
        assertThat(concertDateInfo.concertSchedules().get(0).getConcertDate())
                .isEqualTo(LocalDateTime.of(2024, 12, 12, 8, 50));
        assertThat(concertDateInfo.concertSchedules().get(1).getConcertDate())
                .isEqualTo(LocalDateTime.of(2024, 12, 13, 8, 50));


    }

    @DisplayName("concertScheduleId로 콘서트 좌석을 찾을 수 없습니다.")
    @Test
    void ConcertSeatNotFound() {
        // given
        long concertScheduleId = 3L;
        String key = "concert:seats:" + concertScheduleId;
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(key)).thenReturn(null);
        when(concertQuery.findByConcertScheduleId(concertScheduleId)).thenReturn(Collections.emptyList());

        // when // then
        assertThatThrownBy(()-> concertQueryService.getConcertSeats(concertScheduleId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("콘서트의 좌석을 찾을 수 없습니다.");

    }

    @DisplayName("콘서트 좌석을 찾아 List<seat> 반환")
    @Test
    void ConcertSeatSuccess() {
        // given
        long concertScheduleId = 3L;
        List<Long> seatIds = List.of(1L, 2L);
        String key = "concert:seats:" + concertScheduleId;
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(key)).thenReturn(null);
        when(concertQuery.findByConcertScheduleId(concertScheduleId)).thenReturn(seatIds);

        // when
        SeatInfo concertSeats = concertQueryService.getConcertSeats(concertScheduleId);

        //then
        assertThat(concertSeats.seatIds()).hasSize(2);
        assertThat(concertSeats.seatIds()).containsExactly(1L, 2L);

    }

    @DisplayName("좌석 정보가 존재하지 않음")
    @Test
    void getSeat_Fail() {
        // given
        long seatId = 3L;
        when(concertQuery.getSeat(seatId)).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(()-> concertQueryService.getSeat(seatId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("좌석을 찾을 수 없습니다.");

    }


}