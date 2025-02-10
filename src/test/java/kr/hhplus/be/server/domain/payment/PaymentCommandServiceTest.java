package kr.hhplus.be.server.domain.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentCommand;
import kr.hhplus.be.server.domain.payment.service.PaymentCommandService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceTest {

    @Mock
    private PaymentCommand paymentCommand;

    @InjectMocks
    private PaymentCommandService paymentCommandService;

    @DisplayName("결제 정보를 생성합니다.")
    @Test
    void CreatePaymentReservation() {
        // given
        User user = User.create("유저이름", "email.com");
        Concert concert = Concert.create("공연", "고척돔");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2024,12,12,18,0));
        Seat seat = Seat.create(10,100000L,concertSchedule);
        String tokenId = "TOKEN_ID";
        Reservation reservation = Reservation.create(concertSchedule, user, seat,tokenId);

        Payment payment = Payment.create(user, reservation, 10000L, PaymentStatus.SUCCESS);

        when(paymentCommand.save(any(Payment.class))).thenReturn(payment);

        // when
        Payment result = paymentCommandService.savePayment(user, reservation,10000L, PaymentStatus.SUCCESS);

        // then
        assertEquals(result.getUser().getName(), user.getName());
        assertEquals(result.getAmount(), 10000L);
        assertEquals(result.getStatus(),  PaymentStatus.SUCCESS);
        verify(paymentCommand).save(any(Payment.class));

    }

}