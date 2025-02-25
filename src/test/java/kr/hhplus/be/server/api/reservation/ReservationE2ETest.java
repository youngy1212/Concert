package kr.hhplus.be.server.api.reservation;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import kr.hhplus.be.server.interfaces.api.reservation.dto.CompleteReservationResponse;
import kr.hhplus.be.server.interfaces.api.reservation.dto.PaymentReservationRequest;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.token.service.QueueService;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
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
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private QueueService queueService;

    @DisplayName("콘서트를 예약 (E2E)")
    @Test
    void concertReservationE2E() {
        // given
        User use = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = concertJpaRepository.save(Concert.create("콘서트1", "인스파이어"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert.getId(), LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,  2000L, concertSchedule.getId()));

        queueService.addWaitingQueue(use.getId().toString(), concert.getId().toString());
        queueService.addActiveQueue(use.getId().toString(),1000);

        ReservationRequest reservationRequest = new ReservationRequest(seat.getId(), use.getId(),
                concertSchedule.getId());

        String url = "http://localhost:" + port + "/concert/seats/reservation";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("USER-ID", use.getId().toString());
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
        Seat seat = seatJpaRepository.save(Seat.create(20, 2000L, concertSchedule.getId()));

        queueService.addWaitingQueue(use.getId().toString(), concert.getId().toString());
        queueService.addActiveQueue(use.getId().toString(),1000);

        Reservation reservation = reservationJpaRepository.save(
                Reservation.create(concertSchedule.getId(), use.getId(), seat.getId()));

        PaymentReservationRequest request = PaymentReservationRequest.builder()
                .userId(use.getId())
                .seatId(seat.getId())
                .concertScheduleId(concertSchedule.getId())
                .paymentData("payData")
                .reservationId(reservation.getId()).build();

        String url = "http://localhost:" + port + "/reservation/payment";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("USER-ID", use.getId().toString());
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
