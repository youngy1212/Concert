package kr.hhplus.be.server.domain.reservation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.reservation.repository.ReservationCommand;
import kr.hhplus.be.server.domain.reservation.service.ReservationCommandService;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationCommandServiceTest {

    @InjectMocks
    ReservationCommandService reservationCommandService;

    @Mock
    private ReservationCommand reservationCommand;

    @DisplayName("예약을 생성합니다. 좌석 예약 임시 10분")
    @Test
    void CreateReservation() {
        // given
        User user = User.create("유저이름", "email.com");
        Concert concert = Concert.create("공연", "고척돔");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,18,0));
        Seat seat = Seat.create(10,100000L,concertSchedule);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(50);
        String token = "TOKEN_ID";

       Reservation Reservation = new Reservation(concertSchedule, user, seat, expiresAt,ReservationStatus.RESERVED,token);

        when(reservationCommand.reservationSave(any(Reservation.class))).thenReturn(Reservation);

        // when
        Reservation result = reservationCommandService.createReservation(concertSchedule,
                user, seat,token);

        // then
        assertEquals(result.getUser().getName(), user.getName());
        assertEquals(result.getConcertSchedule().getConcertDate(), concertSchedule.getConcertDate());
        assertEquals(result.getSeat().getSeatNumber(), seat.getSeatNumber());
        verify(reservationCommand).reservationSave(any(Reservation.class));

    }


    @DisplayName("좌석 예약 시간이 지난 경우(만료됨)")
    @Test
    void ReservationIsExpiredFalse() {
        // given
        User user = User.create("유저이름", "email.com");
        Concert concert = Concert.create("공연", "고척돔");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,18,0));
        Seat seat = Seat.create(10,100000L,concertSchedule);
        LocalDateTime time = LocalDateTime.of(2024,12,12,10,0);
        String tokenId = "TOKEN_ID";
        Reservation reservation = new Reservation(concertSchedule,user,seat,time.minusMinutes(10), ReservationStatus.RESERVED,tokenId);

        // when
        boolean expired = reservation.isExpired(time);

        // then
        assertTrue(expired);
    }

    @DisplayName("좌석 예약 시간이 지나지 않은 경우(예약중)")
    @Test
    void ReservationIsExpiredTure() {
        // given
        User user = User.create("유저이름", "email.com");
        Concert concert = Concert.create("공연", "고척돔");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,18,0));
        Seat seat = Seat.create(10,100000L,concertSchedule);
        LocalDateTime time = LocalDateTime.of(2024,12,12,10,0);
        String tokenId = "TOKEN_ID";
        Reservation reservation = new Reservation(concertSchedule,user,seat,time.plusMinutes(10), ReservationStatus.RESERVED,tokenId);

        // when
        boolean expired = reservation.isExpired(time);

        // then
        assertFalse(expired);
    }



}