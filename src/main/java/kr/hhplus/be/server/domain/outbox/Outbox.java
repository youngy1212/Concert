package kr.hhplus.be.server.domain.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Setter
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id")
    private Long id;

    private String eventKey;

    private String eventType;

    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus outboxStatus;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public Outbox(String eventKey, String eventType, String payload, OutboxStatus outboxStatus) {
        this.eventKey = eventKey;
        this.eventType = eventType;
        this.payload = payload;
        this.outboxStatus = outboxStatus;
    }

    public static Outbox create(String eventKey, String eventType, String payload) {
        return Outbox.builder()
                .eventKey(eventKey)
                .eventType(eventType)
                .payload(payload)
                .outboxStatus(OutboxStatus.INIT)
                .build();
    }

    public void publish() {
        this.outboxStatus = OutboxStatus.SUCCESS;
    }
}
