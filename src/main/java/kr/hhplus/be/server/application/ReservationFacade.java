package kr.hhplus.be.server.application;


import kr.hhplus.be.server.annotation.DistributedLock;
import kr.hhplus.be.server.application.dto.ReservationInfo;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.service.ConcertQueryService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.service.ReservationCommandService;
import kr.hhplus.be.server.domain.reservation.service.ReservationQueryService;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    //좌석 예약
    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;
    private final ConcertQueryService concertQueryService;
    private final UserQueryService userQueryService;


    @DistributedLock(key = "'reservation:' + #concertScheduleId + ':' + #seatId")
    @Transactional
    public ReservationInfo reserveSeat(Long userId, Long seatId , Long concertScheduleId, String tokenId) {

        User user = userQueryService.getUserById(userId);
        ConcertSchedule concertSchedule = concertQueryService.getConcertSchedule(concertScheduleId);
        Seat seat = concertQueryService.getSeat(seatId);

        reservationQueryService.existingReservation(concertScheduleId,seatId);
        Reservation reservation = reservationCommandService.createReservation(concertSchedule, user,
                seat, tokenId);

        return new ReservationInfo(reservation.getId(),reservation.getSeat().getId());
    }


}
