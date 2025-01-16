package kr.hhplus.be.server.domain.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertSchedule concertSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private LocalDateTime expiresAt; //만료 시간

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String queueTokenId;

    @Builder
    public Reservation( ConcertSchedule concertSchedule, User user, Seat seat,
                       LocalDateTime expiresAt, ReservationStatus status, String queueTokenId) {
        this.concertSchedule = concertSchedule;
        this.user = user;
        this.seat = seat;
        this.expiresAt = expiresAt;
        this.status = status;
        this.queueTokenId = queueTokenId;
    }

    public static Reservation create(ConcertSchedule concertSchedule, User user, Seat seat, String queueTokenId) {
        return Reservation.builder()
                .concertSchedule(concertSchedule)
                .user(user)
                .seat(seat)
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

}
