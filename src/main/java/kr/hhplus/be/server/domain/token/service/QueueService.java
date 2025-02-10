package kr.hhplus.be.server.domain.token.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private static final String WAITING_TOKENS_KEY = "waiting-tokens";
    private static final String ACTIVE_TOKENS_KEY = "active-tokens";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public QueueService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 대기열에 추가
    public Long addWaitingQueue(String userId, String concertId) {
        String queueTokenId = UUID.randomUUID().toString();
        long score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(WAITING_TOKENS_KEY, userId, score);

        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("userId", userId);
        tokenData.put("enqueuedAt", String.valueOf(score));
        tokenData.put("status", "ENQUEUED");
        tokenData.put("concertId", concertId);

        redisTemplate.opsForHash().putAll("token:" + queueTokenId, tokenData);

        return getWaitingRank(userId);
    }

    public Long getWaitingRank(String userId) {
        Long rank = redisTemplate.opsForZSet().rank(WAITING_TOKENS_KEY, userId);
        if (rank != null) {
            return rank + 1;
        } else {
            throw new IllegalStateException("현재 순위를 가져올 수 없습니다.");
        }
    }

    // 활성으로 이동
    public Set<String> transferWaitingToActive(int count) {
        Set<ZSetOperations.TypedTuple<String>> tokens = redisTemplate.opsForZSet().popMin(WAITING_TOKENS_KEY, count);
        Set<String> userIds = tokens.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .collect(Collectors.toSet());

        if (!userIds.isEmpty()) {
            for (String userId : userIds) {
                addActiveQueue(userId, 600); // 10분 TTL
            }
        }
        return userIds;
    }

    // 활성화 토큰에 TTL 설정하여 추가
    public void addActiveQueue(String userId, long ttlSeconds) {
        String key = "active-token:" + userId;
        redisTemplate.opsForValue().set(key, "active", ttlSeconds, TimeUnit.SECONDS);
        redisTemplate.opsForSet().add(ACTIVE_TOKENS_KEY, userId);
    }

    @Scheduled(fixedDelay = 60000)
    public void removeExpiredActiveQueue() {
        Set<String> activeTokens = redisTemplate.opsForSet().members(ACTIVE_TOKENS_KEY);
        if (activeTokens != null) {
            for (String userId : activeTokens) {
                String key = "active-token:" + userId;
                Boolean exists = redisTemplate.hasKey(key);
                if (exists != null && !exists) {
                    redisTemplate.opsForSet().remove(ACTIVE_TOKENS_KEY, userId);
                }
            }
        }

    }

    //활성 유저 체크
    public boolean isUserActive(String userId) {
        Boolean isMember =redisTemplate.opsForSet().isMember(ACTIVE_TOKENS_KEY, userId);
        return Boolean.TRUE.equals(isMember);
    }
}
