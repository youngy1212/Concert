package kr.hhplus.be.server.api.concert;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.api.concert.dto.ConcertDateResponse;
import kr.hhplus.be.server.api.concert.dto.SeatResponse;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.token.QueueTokenJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ConcertE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private QueueTokenJpaRepository queueTokenJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @DisplayName("콘서트 일정을 조회해온다.(E2E)")
    @Test
    void getConcertDatesE2E() {
        // given
        User use = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = concertJpaRepository.save(Concert.create("콘서트1", "인스파이어"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,0)));
        QueueToken token = queueTokenJpaRepository.save(QueueToken.create(use, concert));

        String url = "http://localhost:" + port + "/concert/date/" + concert.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("QUEUE-TOKEN", token.getQueueTokenId());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<ConcertDateResponse[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                ConcertDateResponse[].class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ConcertDateResponse[] body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body[0].getConcertDate()).isEqualTo(concertSchedule.getConcertDate());
        assertThat(body[0].getConcertScheduleId()).isEqualTo(concertSchedule.getId());

    }

    @DisplayName("콘서트 좌석을 조회해온다.(E2E)")
    @Test
    void getConcertSeatsE2E() {
        // given
        User use = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = concertJpaRepository.save(Concert.create("콘서트1", "인스파이어"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,0)));
        QueueToken token = queueTokenJpaRepository.save(QueueToken.create(use, concert));
        Seat seat1 = seatJpaRepository.save(Seat.create(20, SeatStatus.RESERVED, 2000L, concertSchedule));
        Seat seat2 = seatJpaRepository.save(Seat.create(21, SeatStatus.RESERVED, 2000L, concertSchedule));


        String url = "http://localhost:" + port + "/concert/seats/" + concertSchedule.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("QUEUE-TOKEN", token.getQueueTokenId());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<SeatResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                SeatResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        SeatResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getSeatIds()).isEqualTo(List.of(seat1.getId(),seat2.getId()));

    }

}