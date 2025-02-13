package kr.hhplus.be.server.domain.concert.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "concertSchedule",
        indexes = {
                @Index(name = "idx_concert_id", columnList = "concert_id")
        }
)
public class ConcertSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_schedule_id")
    private Long id;

    private Long concertId;

    private LocalDateTime concertDate;

    @Builder
    private ConcertSchedule(Long concertId, LocalDateTime concertDate) {
        this.concertId = concertId;
        this.concertDate = concertDate;
    }

    public static ConcertSchedule  create(Long concertId,LocalDateTime concertDate){
        return ConcertSchedule.builder().concertId(concertId).concertDate(concertDate).build();
    }


}
