package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service

@RequiredArgsConstructor
public class PaymentCommandService {

    private final PaymentCommand paymentCommand;

    public Payment savePayment(Long userId, Long reservationId, Long amount, PaymentStatus paymentStatus){

        Payment payment = Payment.create(userId, reservationId, amount, paymentStatus);

        return paymentCommand.save(payment);
    }


}
