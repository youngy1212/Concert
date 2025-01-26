package kr.hhplus.be.server.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.token.QueueTokenJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class QueueTokenConcurrencyTest {


    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertQueueTokenFacade concertQueueTokenFacade;

    @Autowired
    private QueueTokenJpaRepository queueTokenJpaRepository;

    @BeforeEach
    void tearDown() {
        queueTokenJpaRepository.deleteAllInBatch();
    }

    @DisplayName("동시에 여러 요청이 오더라고 토큰은 콘서트당 하나만 존재한다.")
    @Test
    void testConcurrentIssueQueueToken() throws InterruptedException {
        // given
        User saveUse = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Concert concert = concertJpaRepository.save(Concert.create("콘서트1", "인스파이어"));
        int numberOfThreads = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        for(int i = 0; i < numberOfThreads; i++) {

            executorService.submit(() -> {
                try {
                    concertQueueTokenFacade.issueQueueToken(saveUse.getId(), concert.getId());
                }  finally {
                    latch.countDown();
                }
            });

        }

        latch.await();
        executorService.shutdown();

        List<QueueToken> tokens = queueTokenJpaRepository.findAll();
        assertEquals(1, tokens.size());

    }
}