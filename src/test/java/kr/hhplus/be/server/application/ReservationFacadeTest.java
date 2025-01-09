package kr.hhplus.be.server.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import kr.hhplus.be.server.api.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.api.reservation.dto.TempReservationResponse;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatStatus;
import kr.hhplus.be.server.domain.reservation.TemporaryReservation;
import kr.hhplus.be.server.domain.token.QueueToken;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.payment.PaymentJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.TemporaryReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.token.QueueTokenJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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
class ReservationFacadeTest {


    @Autowired
    private ReservationFacade reservationFacade;

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
    private TemporaryReservationJpaRepository temporaryReservationJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private PaymentJpaRepository paymentJpaRepository;


    @AfterEach
    void tearDown() {
        temporaryReservationJpaRepository.deleteAllInBatch();
        queueTokenJpaRepository.deleteAllInBatch();
        paymentJpaRepository.deleteAllInBatch();
        reservationJpaRepository.deleteAllInBatch();
        seatJpaRepository.deleteAllInBatch();
        ConcertScheduleJpaRepository.deleteAllInBatch();
        ConcertJpaRepository.deleteAllInBatch();
        UserJpaRepository.deleteAllInBatch();

    }

    @DisplayName("임시예약 요청시 잘못된 유저 정보 요청")
    @Test
    void tempReserveSeatNotUser() {

        // given
        User saveUse = UserJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = ConcertJpaRepository.save(Concert.create("콘서트1","인스파이어"));
        ConcertSchedule concertSchedule = ConcertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,00)));
        Seat seat = seatJpaRepository.save(Seat.create(20, SeatStatus.RESERVED, 2000L, concertSchedule));
        String tokenId = "TOKEN_ID";
        long userId = 9999L;



        // when //then
        assertThatThrownBy(()-> reservationFacade.tempReserveSeat(saveUse.getId(), seat.getSeatId(),
                concertSchedule.getId(), tokenId))
                .isInstanceOf(CustomException.class)
                .hasMessage("유저를 찾을 수 없습니다.");

    }

    @DisplayName("임시예약 요청시 잘못된 콘서트 정보 요청")
    @Test
    void tempReserveSeatNotConcertSchedule() {

        // given
        User saveUse = UserJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = ConcertJpaRepository.save(Concert.create("콘서트1","인스파이어"));
        ConcertSchedule concertSchedule = ConcertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,00)));
        Seat seat = seatJpaRepository.save(Seat.create(20, SeatStatus.RESERVED, 2000L, concertSchedule));
        String tokenId = "TOKEN_ID";
        long ConcertSchedule = 9999L;


        // when //then
        assertThatThrownBy(()-> reservationFacade.tempReserveSeat(saveUse.getId(), seat.getSeatId(),
                concertSchedule.getId(), tokenId))
                .isInstanceOf(CustomException.class)
                .hasMessage("콘서트 일정을 찾을 수 없습니다.");

    }

    @DisplayName("이미 선택한 좌석일 경우")
    @Test
    void tempReserveSeatAlreadyReserved() {

        // given
        User saveUse = UserJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = ConcertJpaRepository.save(Concert.create("콘서트1","인스파이어"));
        ConcertSchedule concertSchedule = ConcertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,00)));
        Seat seat = seatJpaRepository.save(Seat.create(20, SeatStatus.RESERVED, 2000L, concertSchedule));
        String tokenId = "TOKEN_ID";
        long ConcertSchedule = 9999L;


        // when //then
        assertThatThrownBy(()-> reservationFacade.tempReserveSeat(saveUse.getId(), seat.getSeatId(),
                concertSchedule.getId(), tokenId))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 선택된 좌석입니다.");

    }

    @DisplayName("좌석을 성공적으로 임시 예약")
    @Test
    void tempReserveSeatSuccess() {

        // given
        User saveUse = UserJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = ConcertJpaRepository.save(Concert.create("콘서트1","인스파이어"));
        ConcertSchedule concertSchedule = ConcertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,00)));
        Seat seat = seatJpaRepository.save(Seat.create(20, SeatStatus.AVAILABLE, 2000L, concertSchedule));
        String tokenId = "TOKEN_ID";

        // when
        TempReservationResponse tempReservationResponse = reservationFacade.tempReserveSeat(saveUse.getId(), seat.getSeatId(),
                concertSchedule.getId(), tokenId);


        assertNotNull(tempReservationResponse);

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
        TemporaryReservation tempReservation = temporaryReservationJpaRepository.save(
                TemporaryReservation.create(concertSchedule, saveUse, seat, LocalDateTime.now().plusMinutes(5),
                        queueToken.getQueueTokenId()));
        String payData = "AA";


        // When
        ReservationResponse reservationResponse = paymentFacade.completeReservation(
                saveUse.getId(), concert.getId(), seat.getSeatId() , queueToken.getQueueTokenId(), tempReservation.getId(),payData);


        // Then
        assertNotNull(reservationResponse);
        //어떻게 알아야되징...

    }

    @DisplayName("좌석 정보가 불일치")
    @Test
    public void completeReservation_FailSeat() {
        // Given
        User saveUse = UserJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = ConcertJpaRepository.save(Concert.create("콘서트1","인스파이어"));
        ConcertSchedule concertSchedule = ConcertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,00)));
        Seat seat = seatJpaRepository.save(Seat.create(20, SeatStatus.RESERVED , 2000L, concertSchedule));
        Seat seat2 = seatJpaRepository.save(Seat.create(21, SeatStatus.RESERVED , 2000L, concertSchedule));
        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.create(saveUse, concert));
        TemporaryReservation tempReservation = temporaryReservationJpaRepository.save(
                TemporaryReservation.create(concertSchedule, saveUse, seat, LocalDateTime.now().plusMinutes(5),
                        queueToken.getQueueTokenId()));
        String payData = "AA";


        // when //then
        assertThatThrownBy(()-> paymentFacade.completeReservation(saveUse.getId(), concert.getId(), seat2.getSeatId(), queueToken.getQueueTokenId(), tempReservation.getId(),payData))
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
        ConcertSchedule concertSchedule = ConcertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,00)));
        Seat seat = seatJpaRepository.save(Seat.create(20, SeatStatus.RESERVED , 2000L, concertSchedule));
        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.create(saveUse, concert));
        TemporaryReservation tempReservation = temporaryReservationJpaRepository.save(
                TemporaryReservation.create(concertSchedule, saveUse, seat, LocalDateTime.now().plusMinutes(5),
                        queueToken.getQueueTokenId()));
        String payData = "AA";


        // when //then
        assertThatThrownBy(()-> paymentFacade.completeReservation(user.getId(), concert.getId(), seat.getSeatId(), queueToken.getQueueTokenId(), tempReservation.getId(),payData))
                .isInstanceOf(CustomException.class)
                .hasMessage("예약 정보가 일치하지 않습니다.");

    }


}