package kr.hhplus.be.server.domain.outbox;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutBoxScheduler {

    private final OutboxService outboxService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void reprocessInitState() {

        LocalDateTime beforeTime = LocalDateTime.now().minusMinutes(5);

        // init 상태이며, 5분 이상 경과한 항목들을 조회
        List<Outbox> initBefore = outboxService.findInitBefore(beforeTime);

        for (Outbox entry : initBefore) {
            try {
                outboxService.publish(entry.getEventKey());
            } catch (Exception e) {
                log.error("아웃박스 항목 재실행 중 오류 발생: {}", entry.getEventKey(), e);
            }
        }
    }

}
