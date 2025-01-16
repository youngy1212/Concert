package kr.hhplus.be.server.application;


import java.util.List;
import kr.hhplus.be.server.application.dto.ReservationDto;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.service.ConcertCommandService;
import kr.hhplus.be.server.domain.concert.service.ConcertQueryService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
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
    private final ConcertCommandService concertCommandService;
    private final UserQueryService userQueryService;

    @Transactional
    public ReservationDto reserveSeat(Long userId, Long seatId , Long ConcertScheduleId, String tokenId) {

        User user = userQueryService.getUserById(userId);
        ConcertSchedule concertSchedule = concertQueryService.getConcertSchedule(ConcertScheduleId);

        List<ReservationStatus> statuses = List.of(ReservationStatus.RESERVED, ReservationStatus.BOOKED);
        boolean existing = reservationQueryService.existingReservation(ConcertScheduleId,seatId,statuses );

        if (existing) {
            throw new CustomException("이미 선택된 좌석입니다.");
        }


        Seat seat = concertCommandService.findByIdLock(seatId);
        Reservation reservation = reservationCommandService.createReservation(concertSchedule, user,
                seat, tokenId);

        return new ReservationDto(reservation.getId(),reservation.getSeat().getId());
    }



}
