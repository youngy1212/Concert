package kr.hhplus.be.server.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import kr.hhplus.be.server.application.dto.ReservationInfo;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.payment.PaymentJpaRepository;
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
class ReservationFacadeTest {


    @Autowired
    private ReservationFacade reservationFacade;

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
    private PaymentJpaRepository paymentJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;


    @BeforeEach
    void tearDown() {
        paymentJpaRepository.deleteAllInBatch();
        reservationJpaRepository.deleteAllInBatch();
        seatJpaRepository.deleteAllInBatch();
        concertScheduleJpaRepository.deleteAllInBatch();
        concertJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();

    }

    @DisplayName("예약 요청시 잘못된 유저 정보 요청")
    @Test
    void reserveSeatNotUser() {

        // given
        Long concertId = 1L;
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20, 2000L, concertSchedule.getId()));
        String tokenId = "TOKEN_ID";
        long userId = 9999L;

        // when //then
        assertThatThrownBy(()-> reservationFacade.reserveSeat(userId, seat.getId(),
                concertSchedule.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("유저를 찾을 수 없습니다.");

    }

    @DisplayName("임시예약 요청시 잘못된 콘서트 정보 요청")
    @Test
    void reserveSeatNotConcertSchedule() {

        // given
        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Long concertId = 1L;
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,  2000L, concertSchedule.getId()));
        String tokenId = "TOKEN_ID";
        long ConcertSchedule = 9999L;


        // when //then
        assertThatThrownBy(()-> reservationFacade.reserveSeat(saveUse.getId(), seat.getId(),
                ConcertSchedule))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("콘서트 일정을 찾을 수 없습니다.");

    }

    @DisplayName("이미 선택한 좌석일 경우")
    @Test
    void reserveSeatAlreadyReserved() {

        // given
        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Long concertId = 1L;
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,  2000L, concertSchedule.getId()));
        String tokenId = "TOKEN_ID";
        reservationJpaRepository.save(Reservation.create(concertSchedule.getId(),saveUse.getId(),seat.getId()));

        // when //then
        assertThatThrownBy(()-> reservationFacade.reserveSeat(saveUse.getId(), seat.getId(),
                concertSchedule.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 선택된 좌석입니다.");

    }

    @DisplayName("좌석을 성공적으로 임시 예약")
    @Test
    void reserveSeatSuccess() {

        // given
        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Long concertId = 1L;
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20, 2000L, concertSchedule.getId()));
        String tokenId = "TOKEN_ID";

        // when
        ReservationInfo ReservationInfo = reservationFacade.reserveSeat(saveUse.getId(), seat.getId(),
                concertSchedule.getId());

        assertNotNull(ReservationInfo);

    }



}