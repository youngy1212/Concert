package kr.hhplus.be.server.domain.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reservation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_reservation_concert_schedule_seat_status",
                        columnNames = {"concert_schedule_id", "seat_id", "status"}
                )
        }
)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    private Long concertScheduleId;

    private Long userId;

    private Long seatId;

    private LocalDateTime expiresAt; //만료 시간

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String queueTokenId;

    @Builder
    public Reservation(Long concertScheduleId, Long userId, Long seatId,
                       LocalDateTime expiresAt, ReservationStatus status, String queueTokenId) {
        this.concertScheduleId = concertScheduleId;
        this.userId = userId;
        this.seatId = seatId;
        this.expiresAt = expiresAt;
        this.status = status;
        this.queueTokenId = queueTokenId;
    }

    public static Reservation create(Long concertScheduleId, Long userId, Long seatId, String queueTokenId) {
        return Reservation.builder()
                .concertScheduleId(concertScheduleId)
                .userId(userId)
                .seatId(seatId)
                .queueTokenId(queueTokenId)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .status(ReservationStatus.RESERVED).build();
    }

    //예약 만료 확인 (만료시 true)
    public boolean isExpired(LocalDateTime time){
        return expiresAt != null && expiresAt.isBefore(time);
    }

    public void book() {
        this.status = ReservationStatus.BOOKED;
    }

    //예약 진행
    public void expired() {
        this.status = ReservationStatus.EXPIRED;
    }

    // 예약 정보 검증
    public void validateReservation(Long user, Long seat) {
        if (!this.userId.equals(user)) {
            throw new CustomException(HttpStatus.CONFLICT, "예약 정보가 일치하지 않습니다.");
        }

        if (!this.seatId.equals(seat)) {
            throw new CustomException(HttpStatus.CONFLICT, "예약 정보가 일치하지 않습니다.");
        }
    }


}
