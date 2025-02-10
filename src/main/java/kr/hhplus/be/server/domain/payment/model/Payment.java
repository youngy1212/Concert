package kr.hhplus.be.server.domain.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private Long amount;

    private PaymentStatus status;

    private Long userId;

    private Long reservationId;

    @Builder
    private Payment(Long userId, Long reservationId, Long amount, PaymentStatus status) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.status = status;
    }

    public static Payment create(Long userId, Long reservationId, Long amount, PaymentStatus status) {
        return Payment.builder()
                .userId(userId)
                .reservationId(reservationId)
                .amount(amount)
                .status(status)
                .build();
    }



}
