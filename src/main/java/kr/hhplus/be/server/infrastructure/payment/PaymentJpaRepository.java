package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
