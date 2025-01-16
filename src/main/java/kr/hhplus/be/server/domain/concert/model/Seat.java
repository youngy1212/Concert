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
import kr.hhplus.be.server.domain.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    private int seatNumber;

    private SeatStatus status;

    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertSchedule concertSchedule;

    @Builder
    private Seat( int seatNumber, SeatStatus status, Long price,ConcertSchedule concertSchedule) {
        this.seatNumber = seatNumber;
        this.status = status;
        this.price = price;
        this.concertSchedule = concertSchedule;
    }

    public static Seat create(int seatNumber, SeatStatus status, Long price,ConcertSchedule concertSchedule) {
        return Seat.builder()
                .seatNumber(seatNumber)
                .status(status)
                .price(price)
                .concertSchedule(concertSchedule)
                .build();
    }


    // === 상태 전이 메서드 ===
    //임시 예약
    public void reserve() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 선택된 좌석입니다.");
        }
        this.status = SeatStatus.RESERVED;
    }

    //좌석 예약 취소
    public void cancelReserve() {
        if (this.status != SeatStatus.RESERVED) {
            throw new CustomException("좌석이 예약된 상태가 아닙니다.");
        }
        this.status = SeatStatus.AVAILABLE;
    }

    //좌석 확정 예약
    public void book() {
        if (this.status != SeatStatus.RESERVED) {
            throw new CustomException("좌석이 임시 예약된 상태가 아닙니다.");
        }
        this.status = SeatStatus.BOOKED;
    }

    //예약 진행
    public void pending() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new CustomException(HttpStatus.CONFLICT, "해당 좌석은 선택할 수 없습니다.");
        }
        this.status = SeatStatus.PENDING;
    }


}
