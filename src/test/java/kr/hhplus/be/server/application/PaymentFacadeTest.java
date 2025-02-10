package kr.hhplus.be.server.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import kr.hhplus.be.server.application.dto.PaymentReservationInfo;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.token.service.QueueService;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.user.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class PaymentFacadeTest {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @BeforeEach
    void tearDown() {
        reservationJpaRepository.deleteAllInBatch();
        seatJpaRepository.deleteAllInBatch();
        concertScheduleJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
    }

    @DisplayName("예약이 정상적으로 성공")
    @Test
    public void completeReservation_Success() {
        // Given
        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Long concertId = 1L;
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20, 2000L, concertSchedule.getId()));
        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule.getId(), saveUse.getId(), seat.getId()));
        String payData = "AA";

        queueService.addWaitingQueue(saveUse.getId().toString(), concertId.toString());
        queueService.addActiveQueue(saveUse.getId().toString(),1000);


        // When
        PaymentReservationInfo paymentReservationInfo = paymentFacade.completeReservation(
                saveUse.getId(), concertSchedule.getId(), seat.getId() ,reservation.getId(),payData);


        // Then
        assertNotNull(paymentReservationInfo);
        assertThat(paymentReservationInfo.seatId()).isEqualTo(seat.getId());

    }

    @DisplayName("좌석 정보가 불일치")
    @Test
    public void completeReservation_FailSeat() {
        // Given
        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Long concertId = 1L;
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,2000L, concertSchedule.getId()));
        Seat seat2 = seatJpaRepository.save(Seat.create(21, 2000L, concertSchedule.getId()));
        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule.getId(), saveUse.getId(), seat.getId()));
        String payData = "AA";

        queueService.addWaitingQueue(saveUse.getId().toString(), concertId.toString());
        queueService.addActiveQueue(saveUse.getId().toString(),1000);


        // when //then
        assertThatThrownBy(()-> paymentFacade.completeReservation(saveUse.getId(), concertSchedule.getId(), seat2.getId(), reservation.getId(),payData))
                .isInstanceOf(CustomException.class)
                .hasMessage("예약 정보가 일치하지 않습니다.");

    }


    @DisplayName("유저 정보가 불일치")
    @Test
    public void completeReservation_FailUser() {
        // Given
        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        User user = userJpaRepository.save(User.create("유저2", "eamil@naemver"));
        Long concertId = 1L;
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,  2000L, concertSchedule.getId()));

        queueService.addWaitingQueue(user.getId().toString(), concertId.toString());
        queueService.addActiveQueue(user.getId().toString(),1000);

        Reservation reservation = reservationJpaRepository.save(
                Reservation.create(concertSchedule.getId(), saveUse.getId(), seat.getId()));
        String payData = "AA";


        // when //then
        assertThatThrownBy(()-> paymentFacade.completeReservation(user.getId(), concertSchedule.getId(), seat.getId(), reservation.getId(),payData))
                .isInstanceOf(CustomException.class)
                .hasMessage("예약 정보가 일치하지 않습니다.");

    }

}