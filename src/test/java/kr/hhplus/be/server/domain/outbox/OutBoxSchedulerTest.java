package kr.hhplus.be.server.domain.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OutBoxSchedulerTest {

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private OutBoxScheduler outBoxScheduler;


    @DisplayName("5분 넘은 미처리 이벤트 다시 이벤트 발행")
    @Test
    void reprocessInitStateTest() {
        // given
        Outbox outbox1 = new Outbox();
        outbox1.setEventKey("eventKey1");
        outbox1.setOutboxStatus(OutboxStatus.INIT);
        outbox1.setCreatedDate(LocalDateTime.now().minusMinutes(6));
        outboxRepository.save(outbox1);

        Outbox outbox2 = new Outbox();
        outbox2.setEventKey("eventKey2");
        outbox2.setOutboxStatus(OutboxStatus.INIT);
        outbox2.setCreatedDate(LocalDateTime.now().minusMinutes(1));
        outboxRepository.save(outbox2);

        // when
        outBoxScheduler.reprocessInitState();
        Outbox reprocessOutbox1 = outboxRepository.getByEventKey("eventKey1");
        Outbox unprocessOutbox2 = outboxRepository.getByEventKey("eventKey2");

        // then
        assertThat(reprocessOutbox1.getOutboxStatus()).isEqualTo(OutboxStatus.SUCCESS);
        assertThat(unprocessOutbox2.getOutboxStatus()).isEqualTo(OutboxStatus.INIT);


    }


}