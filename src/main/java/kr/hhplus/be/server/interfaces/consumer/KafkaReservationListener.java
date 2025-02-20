package kr.hhplus.be.server.interfaces.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.domain.outbox.OutboxService;
import kr.hhplus.be.server.infrastructure.external.DataPlatformSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Log4j2
public class KafkaReservationListener {

    private final OutboxService outboxService;
    private final DataPlatformSender dataPlatformSender;

    @KafkaListener(groupId = "outbox-group", topics = "concert-reserved")
    public void consumeOutbox(ConsumerRecord<String, String> data, Acknowledgment acknowledgment, Consumer<String, String> consumer){
        final String eventKey = data.key();
        outboxService.publish(eventKey);
        acknowledgment.acknowledge();
    }

    @KafkaListener(groupId = "data-platform-group", topics = "concert-reserved")
    public void consumeExternalPlatform(ConsumerRecord<String, String> data, Acknowledgment acknowledgment, Consumer<String, String> consumer) throws JsonProcessingException {
        // 외부 플랫폼 연동
        dataPlatformSender.sendReservationData();
        acknowledgment.acknowledge();
    }

}
