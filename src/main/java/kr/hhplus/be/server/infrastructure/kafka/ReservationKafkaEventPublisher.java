package kr.hhplus.be.server.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationKafkaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String key,String message){
        kafkaTemplate.send(topic,key, message);
    }

}
