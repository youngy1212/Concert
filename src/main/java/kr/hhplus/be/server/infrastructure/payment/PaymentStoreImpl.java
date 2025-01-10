package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class PaymentStoreImpl implements PaymentStore {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment savePayment(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
