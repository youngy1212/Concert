package kr.hhplus.be.server.domain.concert.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    private int seatNumber;

    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertSchedule concertSchedule;

    @Builder
    private Seat( int seatNumber,  Long price,ConcertSchedule concertSchedule) {
        this.seatNumber = seatNumber;
        this.price = price;
        this.concertSchedule = concertSchedule;
    }

    public static Seat create(int seatNumber,Long price,ConcertSchedule concertSchedule) {
        return Seat.builder()
                .seatNumber(seatNumber)
                .price(price)
                .concertSchedule(concertSchedule)
                .build();
    }

}
