package kr.hhplus.be.server.interfaces.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class KafkaTest {

    private final String topic = "test-topic";

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;


    @KafkaListener(topics = topic,groupId = "test-group")
    void testListener(String message) {
        atomicInteger.incrementAndGet();
    }

    @DisplayName("카프카 테스트컨테이너로 연결 테스트")
    @Test
    void KafkaConnectTest() {
        kafkaTemplate.send(topic,"Hello, Kafka!");
        await()
                .pollInterval(Duration.ofMillis(300))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(atomicInteger.get()).isEqualTo(1);
                });
    }


}

