package kr.hhplus.be.server.domain.concert.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "seat",
        indexes = {
                @Index(name = "idx_concert_schedule_id", columnList = "concert_schedule_id")
        }
)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    private int seatNumber;

    private Long price;

    private Long concertScheduleId;

    @Builder
    private Seat( int seatNumber,  Long price,Long concertScheduleId) {
        this.seatNumber = seatNumber;
        this.price = price;
        this.concertScheduleId = concertScheduleId;
    }

    public static Seat create(int seatNumber,Long price,Long concertScheduleId) {
        return Seat.builder()
                .seatNumber(seatNumber)
                .price(price)
                .concertScheduleId(concertScheduleId)
                .build();
    }

}
