package kr.hhplus.be.server.domain.concert.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    @JsonIgnore
    private Concert concert;

    private LocalDateTime concertDate;

    @Builder
    private ConcertSchedule( Concert concert, LocalDateTime concertDate) {
        this.concert = concert;
        this.concertDate = concertDate;
    }

    public static ConcertSchedule  create(Concert concert,LocalDateTime concertDate){
        return ConcertSchedule.builder().concert(concert).concertDate(concertDate).build();
    }


}
