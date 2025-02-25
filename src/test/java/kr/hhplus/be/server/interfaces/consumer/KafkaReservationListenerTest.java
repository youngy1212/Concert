package kr.hhplus.be.server.interfaces.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import java.time.Duration;
import java.time.LocalDateTime;
import kr.hhplus.be.server.application.PaymentFacade;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.OutboxStatus;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.kafka.ReservationKafkaEventPublisher;
import kr.hhplus.be.server.infrastructure.outbox.OutBoxJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Log4j2
class KafkaReservationListenerTest {


    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private OutBoxJpaRepository outBoxJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @MockitoBean
    private ReservationKafkaEventPublisher reservationKafkaEventPublisher;

    @BeforeEach
    void tearDown() {
        reservationJpaRepository.deleteAllInBatch();
        seatJpaRepository.deleteAllInBatch();
        concertScheduleJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
    }

    @DisplayName("외부데이터전송 카프카 이벤트 발행 확인")
    @Test
    void reservationEventPublisherTest() {
        // Given
        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Long concertId = 1L;
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,2000L, concertSchedule.getId()));
        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule.getId(), saveUse.getId(), seat.getId()));
        String payData = "AA";


        // when
        paymentFacade.completeReservation(saveUse.getId(), concertSchedule.getId(), seat.getId(), reservation.getId(), payData);

        // then
        // 이벤트 처리 대기
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> Mockito.verify(reservationKafkaEventPublisher, times(1))
                        .sendMessage(eq("concert-reserved"),anyString(), anyString()));
    }

    @DisplayName("외부데이터전송 아웃박스 저장 확인")
    @Test
    void reservationOutboxEventPublisherTest() {
        // Given
        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Long concertId = 1L;
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,2000L, concertSchedule.getId()));
        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule.getId(), saveUse.getId(), seat.getId()));
        String payData = "AA";


        // when
        paymentFacade.completeReservation(saveUse.getId(), concertSchedule.getId(), seat.getId(), reservation.getId(), payData);

        //이벤티 키가 뭔지 모르겟다..

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {

                    Outbox outbox = outBoxJpaRepository.findAll().get(0);
                    assertEquals(OutboxStatus.SUCCESS, outbox.getOutboxStatus() );
                });

    }

}