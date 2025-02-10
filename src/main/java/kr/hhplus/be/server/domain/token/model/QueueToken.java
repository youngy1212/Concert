package kr.hhplus.be.server.domain.token.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_concert",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_concert",
                        columnNames = {"user_id", "concert_id"}
                )
        }
)
public class QueueToken extends BaseEntity {

    @Id
    private String queueTokenId;

    private Long userId;

    private Long concertId;

    private LocalDateTime expiresAt;

    private LocalDateTime enqueuedAt; //대기열에 진입하는 시간

    @Enumerated(EnumType.STRING)
    private QueueTokenStatus status;


    @Builder
    public QueueToken(String queueTokenId, Long userId, Long concertId, LocalDateTime expiresAt,
                      LocalDateTime enqueuedAt,
                      QueueTokenStatus status) {
        this.queueTokenId = queueTokenId;
        this.userId = userId;
        this.concertId = concertId;
        this.expiresAt = expiresAt;
        this.enqueuedAt = enqueuedAt;
        this.status = status;
    }

    public static QueueToken create(Long userId, Long concertId) {
        return QueueToken.builder()
                .queueTokenId(UUID.randomUUID().toString())
                .userId(userId)
                .concertId(concertId)
                .enqueuedAt(LocalDateTime.now())
                .status(QueueTokenStatus.PENDING)
                .build();
    }

    public static QueueToken createInTime(Long userId, Long concertId, LocalDateTime expiresAt) {
        return QueueToken.builder()
                .queueTokenId(UUID.randomUUID().toString())
                .userId(userId)
                .concertId(concertId)
                .expiresAt(expiresAt)
                .status(QueueTokenStatus.PENDING)
                .build();
    }

    //토큰 활성화
    public void tokenActive(LocalDateTime localDateTime){

        if(status == QueueTokenStatus.ACTIVE){
            throw new CustomException("이미 예약중입니다.");
        }

        status = QueueTokenStatus.ACTIVE;
        expiresAt = localDateTime.plusMinutes(10);

    }

    //토큰 만료
    public void tokenExpire(){

        if (status != QueueTokenStatus.ACTIVE && status != QueueTokenStatus.PENDING) {
            throw new CustomException("토큰을 만료시킬 수 없습니다.");
        }
        
        status = QueueTokenStatus.EXPIRED;
    }

    //토크 만료 확인
    public boolean isExpired(){
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

}
