package kr.hhplus.be.server.aspect;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import kr.hhplus.be.server.api.reservation.dto.PaymentReservationRequest;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class AuthorizationAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QueueTokenJpaRepository queueTokenJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;


    @DisplayName("좌석 확정 요청이 들어올때 헤더의 토큰을 검사한다")
    @Test
    void authorizationAspectSuccess() throws Exception {
        // given

        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = concertJpaRepository.save(Concert.create("콘서트1", "인스파이어"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,  2000L, concertSchedule));
        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.createInTime(saveUse,concert,LocalDateTime.now().plusMinutes(20)));
        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule, saveUse, seat, queueToken.getQueueTokenId()));

        PaymentReservationRequest request = PaymentReservationRequest.builder()
                .seatId(seat.getId())
                .tokenId(queueToken.getQueueTokenId())
                .userId(saveUse.getId())
                .reservationId(reservation.getId())
                .concertScheduleId(concertSchedule.getId())
                .paymentData("data")
                .build();


        // then
        mockMvc.perform(
                        post("/reservation/payment")
                                .header("QUEUE-TOKEN", queueToken.getQueueTokenId())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andDo(print()) //로그확인
                .andExpect(status().isOk());

    }

    @DisplayName("잘못된 토큰 접근으로 오류 반환")
    @Test
    void authorizationAspectFail() throws Exception {
        // given

        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = concertJpaRepository.save(Concert.create("콘서트1", "인스파이어"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,2000L, concertSchedule));
        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.create(saveUse,concert));
        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule, saveUse, seat, queueToken.getQueueTokenId()));

        PaymentReservationRequest request = PaymentReservationRequest.builder()
                .seatId(seat.getId())
                .tokenId(queueToken.getQueueTokenId())
                .userId(saveUse.getId())
                .reservationId(reservation.getId())
                .concertScheduleId(concertSchedule.getId())
                .paymentData("data")
                .build();


        // then
        mockMvc.perform(
                        post("/reservation/payment")
                                .header("QUEUE-TOKEN", "")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andDo(print()) //로그확인
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못 된 경로입니다."));

    }

    @DisplayName("만료된 토큰 접근으로 오류 반환")
    @Test
    void authorizationAspectExpired() throws Exception {
        // given

        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = concertJpaRepository.save(Concert.create("콘서트1", "인스파이어"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,10,0)));
        Seat seat = seatJpaRepository.save(Seat.create(20,  2000L, concertSchedule));
        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.createInTime(saveUse,concert,LocalDateTime.now().minusMinutes(20)));

        Reservation reservation = reservationJpaRepository.save(
                Reservation.create(concertSchedule, saveUse, seat, queueToken.getQueueTokenId()));

        PaymentReservationRequest request = PaymentReservationRequest.builder()
                .seatId(seat.getId())
                .tokenId(queueToken.getQueueTokenId())
                .userId(saveUse.getId())
                .reservationId(reservation.getId())
                .concertScheduleId(concertSchedule.getId())
                .paymentData("data")
                .build();


        // then
        mockMvc.perform(
                        post("/reservation/payment")
                                .header("QUEUE-TOKEN", queueToken.getQueueTokenId())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andDo(print()) //로그확인
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("대기시간이 만료되었습니다."));

    }

}