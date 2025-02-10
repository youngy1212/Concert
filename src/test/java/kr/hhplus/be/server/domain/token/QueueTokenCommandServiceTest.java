package kr.hhplus.be.server.domain.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.model.QueueTokenStatus;
import kr.hhplus.be.server.domain.token.repository.QueueTokenCommand;
import kr.hhplus.be.server.domain.token.service.QueueTokenCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QueueTokenCommandServiceTest {

    @Mock
    private QueueTokenCommand queueTokenCommand;

    @InjectMocks
    private QueueTokenCommandService queueTokenCommandService;

    @DisplayName("유저가 QueueToken에 들어와, 대기중 토큰을 발급한다.")
    @Test
    void issueToken_ShouldReturnQueueToken() {
        // given
        long userId = 1L;
        long concertId = 2L;
        QueueToken queueToken = QueueToken.create(userId,concertId);

        when(queueTokenCommand.save(any(QueueToken.class))).thenReturn(queueToken);

        // when
        QueueToken result = queueTokenCommandService.issueToken(userId, concertId);

        // then
        assertEquals(userId, result.getUserId());
        assertEquals(concertId, result.getConcertId());
        assertEquals(result.getStatus(), QueueTokenStatus.PENDING);
        verify(queueTokenCommand).save(any(QueueToken.class));

    }

}