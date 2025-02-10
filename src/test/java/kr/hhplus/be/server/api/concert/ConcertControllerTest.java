package kr.hhplus.be.server.api.concert;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.service.ConcertQueryService;
import kr.hhplus.be.server.domain.concert.service.dto.ConcertDateInfo;
import kr.hhplus.be.server.domain.concert.service.dto.SeatInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConcertQueryService concertQueryService;

    @DisplayName("콘서트를 조회한다. ConcertDateResponse List 응답")
    @Test
    void getConcertScheduleReturnConcertDateResponseList() throws Exception {
        // given
        long concertId = 1L;
        ConcertSchedule concertSchedule = ConcertSchedule.create(concertId, LocalDateTime.of(2024, 12, 12, 8, 50));
        ConcertSchedule concertSchedule2 = ConcertSchedule.create(concertId,LocalDateTime.of(2024,12,13,8,50));

        List<ConcertSchedule> schedules = List.of(concertSchedule, concertSchedule2);

        ConcertDateInfo chargeDto = new ConcertDateInfo(schedules);
        when(concertQueryService.getAllConcertSchedule(concertId)).thenReturn(chargeDto);

        // when // then
        mockMvc.perform(get("/concert/date/{concertId}",concertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].concertDate").value("2024-12-12T08:50:00"))
                .andExpect(jsonPath("$[1].concertDate").value("2024-12-13T08:50:00"));

    }

    @DisplayName("콘서트를 좌석을 조회합다. SeatResponse 응답")
    @Test
    void getConcertSeatsReturnSeatResponse() throws Exception {
        // given
        long concertScheduleId = 3L;
        List<Long> seatIds = List.of(1L, 2L);
        SeatInfo seatInfo = new SeatInfo(seatIds);
        when(concertQueryService.getConcertSeats(concertScheduleId)).thenReturn(seatInfo);

        // when // then
        mockMvc.perform(get("/concert/seats/{concertScheduleId}",concertScheduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatIds",containsInAnyOrder(1, 2)));

    }

}