package kr.hhplus.be.server.domain.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentCommand;
import kr.hhplus.be.server.domain.payment.service.PaymentCommandService;
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
        Long userId = 1L;
        Long reservationId = 4L;

        Payment payment = Payment.create(userId, reservationId, 10000L, PaymentStatus.SUCCESS);

        when(paymentCommand.save(any(Payment.class))).thenReturn(payment);

        // when
        Payment result = paymentCommandService.savePayment(userId, reservationId,10000L, PaymentStatus.SUCCESS);

        // then
        assertEquals(result.getAmount(), 10000L);
        assertEquals(result.getStatus(),  PaymentStatus.SUCCESS);
        verify(paymentCommand).save(any(Payment.class));

    }

}