package kr.hhplus.be.server.api.token;

import kr.hhplus.be.server.api.token.dto.SwaggerTokenController;
import kr.hhplus.be.server.api.token.dto.TokenResponse;
import kr.hhplus.be.server.api.token.dto.WaitingQueueResponse;
import kr.hhplus.be.server.application.ConcertQueueTokenFacade;
import kr.hhplus.be.server.application.dto.QueueTokenInfo;
import kr.hhplus.be.server.domain.token.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController implements SwaggerTokenController {

    private final ConcertQueueTokenFacade concertQueueTokenFacade;
    private final QueueService queueService;

    @GetMapping("/tokens/{userId}/{concertId}")
    public ResponseEntity<TokenResponse> issueServiceToken(
            @PathVariable Long userId,
            @PathVariable Long concertId
    ){
        QueueTokenInfo queueTokenInfo = concertQueueTokenFacade.issueQueueToken(userId, concertId);
        return ResponseEntity.ok(TokenResponse.of(queueTokenInfo.queueTokenId(), queueTokenInfo.expiresAt()));
    }

    @GetMapping("/waitingQueue/{userId}/{concertId}")
    public ResponseEntity<WaitingQueueResponse> waitingQueue(
            @PathVariable Long userId,
            @PathVariable Long concertId
    ){
        Long rank = queueService.addWaitingQueue(String.valueOf(userId),String.valueOf(concertId));
        return ResponseEntity.ok(WaitingQueueResponse.of(userId, rank));
    }

}
