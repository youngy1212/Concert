package kr.hhplus.be.server.aspect;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class AuthorizationAspectTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserJpaRepository userJpaRepository;
//
//    @Autowired
//    private ReservationJpaRepository reservationJpaRepository;
//
//    @Autowired
//    private ConcertScheduleJpaRepository concertScheduleJpaRepository;
//
//    @Autowired
//    private SeatJpaRepository seatJpaRepository;
//
//
//    @DisplayName("좌석 확정 요청이 들어올때 헤더의 토큰을 검사한다")
//    @Test
//    void authorizationAspectSuccess() throws Exception {
//        // given
//
//        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
//        Long concertId = 1L;
//        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
//        Seat seat = seatJpaRepository.save(Seat.create(20,  2000L, concertSchedule.getId()));
//        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.createInTime(saveUse.getId(),concertId,LocalDateTime.now().plusMinutes(20)));
//        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule.getId(), saveUse.getId(), seat.getId(), queueToken.getQueueTokenId()));
//
//        PaymentReservationRequest request = PaymentReservationRequest.builder()
//                .seatId(seat.getId())
//                .tokenId(queueToken.getQueueTokenId())
//                .userId(saveUse.getId())
//                .reservationId(reservation.getId())
//                .concertScheduleId(concertSchedule.getId())
//                .paymentData("data")
//                .build();
//
//
//        // then
//        mockMvc.perform(
//                        post("/reservation/payment")
//                                .header("QUEUE-TOKEN", queueToken.getQueueTokenId())
//                                .content(objectMapper.writeValueAsString(request))
//                                .contentType(MediaType.APPLICATION_JSON)
//
//                )
//                .andDo(print()) //로그확인
//                .andExpect(status().isOk());
//
//    }
//
//    @DisplayName("잘못된 토큰 접근으로 오류 반환")
//    @Test
//    void authorizationAspectFail() throws Exception {
//        // given
//
//        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
//        Long concertId = 1L;
//        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
//        Seat seat = seatJpaRepository.save(Seat.create(20,2000L, concertSchedule.getId()));
//        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.create(saveUse.getId(),concertId));
//        Reservation reservation = reservationJpaRepository.save(Reservation.create(concertSchedule.getId(), saveUse.getId(), seat.getId(), queueToken.getQueueTokenId()));
//
//        PaymentReservationRequest request = PaymentReservationRequest.builder()
//                .seatId(seat.getId())
//                .tokenId(queueToken.getQueueTokenId())
//                .userId(saveUse.getId())
//                .reservationId(reservation.getId())
//                .concertScheduleId(concertSchedule.getId())
//                .paymentData("data")
//                .build();
//
//
//        // then
//        mockMvc.perform(
//                        post("/reservation/payment")
//                                .header("QUEUE-TOKEN", "")
//                                .content(objectMapper.writeValueAsString(request))
//                                .contentType(MediaType.APPLICATION_JSON)
//
//                )
//                .andDo(print()) //로그확인
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("잘못 된 경로입니다."));
//
//    }
//
//    @DisplayName("만료된 토큰 접근으로 오류 반환")
//    @Test
//    void authorizationAspectExpired() throws Exception {
//        // given
//
//        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
//        Long concertId = 1L;
//        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concertId, LocalDateTime.of(2024,12,12,10,0)));
//        Seat seat = seatJpaRepository.save(Seat.create(20,  2000L, concertSchedule.getId()));
//        QueueToken queueToken = queueTokenJpaRepository.save(QueueToken.createInTime(saveUse.getId(),concertId,LocalDateTime.now().minusMinutes(20)));
//
//        Reservation reservation = reservationJpaRepository.save(
//                Reservation.create(concertSchedule.getId(), saveUse.getId(), seat.getId(), queueToken.getQueueTokenId()));
//
//        PaymentReservationRequest request = PaymentReservationRequest.builder()
//                .seatId(seat.getId())
//                .tokenId(queueToken.getQueueTokenId())
//                .userId(saveUse.getId())
//                .reservationId(reservation.getId())
//                .concertScheduleId(concertSchedule.getId())
//                .paymentData("data")
//                .build();
//
//
//        // then
//        mockMvc.perform(
//                        post("/reservation/payment")
//                                .header("QUEUE-TOKEN", queueToken.getQueueTokenId())
//                                .content(objectMapper.writeValueAsString(request))
//                                .contentType(MediaType.APPLICATION_JSON)
//
//                )
//                .andDo(print()) //로그확인
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("대기시간이 만료되었습니다."));
//
//    }

}