package kr.hhplus.be.server.domain.token.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Testcontainers
class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @DisplayName("대기열 진입 확인")
    @Test
    void testAddTokenToWaitingQueue() {
        // given
        String userId = "user1";
        String concertId = "concert1";

        // when
        queueService.addWaitingQueue(userId,concertId);

        // then
        Double score = redisTemplate.opsForZSet().score("waiting-tokens", userId);
        assertNotNull(score);
    }

    @DisplayName("테스트 전환")
    @Test
    void testTransferTokensToActive() {
        // given
        queueService.addWaitingQueue("user1","concert1");
        queueService.addWaitingQueue("user2","concert2");

        // when
        Set<String> activatedUsers = queueService.transferWaitingToActive(2);

        // then
        assertEquals(2, activatedUsers.size());
        assertTrue(activatedUsers.contains("user1"));
        assertTrue(activatedUsers.contains("user2"));
        Boolean isMember1 = redisTemplate.opsForSet().isMember("active-tokens", "user1");
        Boolean isMember2 = redisTemplate.opsForSet().isMember("active-tokens", "user2");
        assertEquals(Boolean.TRUE, isMember1);
        assertEquals(Boolean.TRUE, isMember2);

    }

    @DisplayName("대기열 진입 후 순위 확인")
    @Test
    void GetWaitingRank() {
        // given
        String userId1 = "user1";
        String userId2 = "user2";
        queueService.addWaitingQueue(userId1,"concert1");
        queueService.addWaitingQueue(userId2,"concert2");

        // when
        Long rank1 = queueService.getWaitingRank(userId1);
        Long rank2 = queueService.getWaitingRank(userId2);

        // then
        assertEquals(1L, rank1);
        assertEquals(2L, rank2);
    }

    @DisplayName("만료된 활성화 토큰 제거 확인")
    @Test
    void removeExpiredActiveQueue() throws InterruptedException {
        // given
        String userId = "user1";
        queueService.addActiveQueue(userId, 1); // TTL을 1초로 설정
        Thread.sleep(2000); // 키가 만료될 때까지 대기

        // when
        queueService.removeExpiredActiveQueue();

        // then
        Boolean isMember = redisTemplate.opsForSet().isMember("active-tokens", userId);
        assertNotEquals(Boolean.TRUE, isMember);

    }


}