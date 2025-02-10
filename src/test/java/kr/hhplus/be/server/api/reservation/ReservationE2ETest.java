package kr.hhplus.be.server.api.reservation;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import kr.hhplus.be.server.api.reservation.dto.CompleteReservationResponse;
import kr.hhplus.be.server.api.reservation.dto.PaymentReservationRequest;
import kr.hhplus.be.server.api.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.api.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
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
public class ReservationE2ETest {

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

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @DisplayName("콘서트를 예약 (E2E)")
    @Test
    void concertReservationE2E() {
        // given
        User use = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = concertJpaRepository.save(Concert.create("콘서트1", "인스파이어"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert.getId(), LocalDateTime.of(2024,12,12,10,0)));
        QueueToken token = queueTokenJpaRepository.save(QueueToken.create(use.getId(), concert.getId()));
        Seat seat = seatJpaRepository.save(Seat.create(20,  2000L, concertSchedule.getId()));

        ReservationRequest reservationRequest = new ReservationRequest(seat.getId(), use.getId(),
                concertSchedule.getId(), token.getQueueTokenId());

        String url = "http://localhost:" + port + "/concert/seats/reservation";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("QUEUE-TOKEN", token.getQueueTokenId());
        HttpEntity<ReservationRequest> entity = new HttpEntity<>(reservationRequest,headers);

        // When
        ResponseEntity<ReservationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                ReservationResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ReservationResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getSeatId()).isEqualTo(seat.getId());

    }


    @DisplayName("결체후예약완료 (E2E)")
    @Test
    void paymentReservationE2E() {
        // given
        User use = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = concertJpaRepository.save(Concert.create("콘서트1", "인스파이어"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert.getId(), LocalDateTime.of(2024,12,12,10,0)));
        QueueToken token = queueTokenJpaRepository.save(QueueToken.create(use.getId(), concert.getId()));
        Seat seat = seatJpaRepository.save(Seat.create(20, 2000L, concertSchedule.getId()));

        Reservation reservation = reservationJpaRepository.save(
                Reservation.create(concertSchedule.getId(), use.getId(), seat.getId(), token.getQueueTokenId()));

        PaymentReservationRequest request = PaymentReservationRequest.builder()
                .userId(use.getId())
                .seatId(seat.getId())
                .tokenId(token.getQueueTokenId())
                .concertScheduleId(concertSchedule.getId())
                .paymentData("payData")
                .reservationId(reservation.getId()).build();

        String url = "http://localhost:" + port + "/reservation/payment";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("QUEUE-TOKEN", token.getQueueTokenId());
        HttpEntity<PaymentReservationRequest> entity = new HttpEntity<>(request,headers);

        // When
        ResponseEntity<CompleteReservationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                CompleteReservationResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CompleteReservationResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getSeatId()).isEqualTo(seat.getId());
        assertThat(body.getConcertScheduleId()).isEqualTo(concertSchedule.getId());
        assertThat(body.getAmount()).isEqualTo(seat.getPrice());

    }

}
