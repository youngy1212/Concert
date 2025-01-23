package kr.hhplus.be.server.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import kr.hhplus.be.server.application.dto.PaymentReservationDto;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.payment.PaymentJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.token.QueueTokenJpaRepository;
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
    private UserJpaRepository UserJpaRepository;

    @Autowired
    private ConcertJpaRepository ConcertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository ConcertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private QueueTokenJpaRepository queueTokenJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;


    @BeforeEach
    void tearDown() {
        queueTokenJpaRepository.deleteAllInBatch();
        paymentJpaRepository.deleteAllInBatch();
        reservationJpaRepository.deleteAllInBatch();
        seatJpaRepository.deleteAllInBatch();
        ConcertScheduleJpaRepository.deleteAllInBatch();
        ConcertJpaRepository.deleteAllInBatch();
        UserJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();

    }

    @DisplayName("예약이 정상적으로 성공")
    @Test
    public void completeReservation_Success() {
        // Given
        User saveUse = UserJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = ConcertJpaRepository.save(Concert.create("콘서트1","인스파이어"));
        ConcertSchedule concertSchedule = ConcertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20, SeatStatus.RESERVED , 2000L, concertSchedule));
        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.create(saveUse, concert));
        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule, saveUse, seat,queueToken.getQueueTokenId()));
        String payData = "AA";


        // When
        PaymentReservationDto paymentReservationDto = paymentFacade.completeReservation(
                saveUse.getId(), concertSchedule.getId(), seat.getId() , queueToken.getQueueTokenId(), reservation.getId(),payData);


        // Then
        assertNotNull(paymentReservationDto);
        assertThat(paymentReservationDto.seatId()).isEqualTo(seat.getId());

    }

    @DisplayName("좌석 정보가 불일치")
    @Test
    public void completeReservation_FailSeat() {
        // Given
        User saveUse = UserJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = ConcertJpaRepository.save(Concert.create("콘서트1","인스파이어"));
        ConcertSchedule concertSchedule = ConcertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20, SeatStatus.RESERVED , 2000L, concertSchedule));
        Seat seat2 = seatJpaRepository.save(Seat.create(21, SeatStatus.RESERVED , 2000L, concertSchedule));
        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.create(saveUse, concert));
        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule, saveUse, seat, queueToken.getQueueTokenId()));
        String payData = "AA";


        // when //then
        assertThatThrownBy(()-> paymentFacade.completeReservation(saveUse.getId(), concertSchedule.getId(), seat2.getId(), queueToken.getQueueTokenId(), reservation.getId(),payData))
                .isInstanceOf(CustomException.class)
                .hasMessage("예약 정보가 일치하지 않습니다.");

    }


    @DisplayName("유저 정보가 불일치")
    @Test
    public void completeReservation_FailUser() {
        // Given
        User saveUse = UserJpaRepository.save(User.create("유저", "eamil@naemver"));
        User user = UserJpaRepository.save(User.create("유저2", "eamil@naemver"));
        Concert concert = ConcertJpaRepository.save(Concert.create("콘서트1","인스파이어"));
        ConcertSchedule concertSchedule = ConcertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20, SeatStatus.RESERVED , 2000L, concertSchedule));
        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.create(saveUse, concert));
        Reservation reservation = reservationJpaRepository.save(
                Reservation.create(concertSchedule, saveUse, seat,queueToken.getQueueTokenId()));
        String payData = "AA";


        // when //then
        assertThatThrownBy(()-> paymentFacade.completeReservation(user.getId(), concertSchedule.getId(), seat.getId(), queueToken.getQueueTokenId(), reservation.getId(),payData))
                .isInstanceOf(CustomException.class)
                .hasMessage("예약 정보가 일치하지 않습니다.");

    }

}