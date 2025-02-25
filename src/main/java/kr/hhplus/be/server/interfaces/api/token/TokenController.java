package kr.hhplus.be.server.interfaces.api.token;

import kr.hhplus.be.server.interfaces.api.token.dto.SwaggerTokenController;
import kr.hhplus.be.server.interfaces.api.token.dto.WaitingQueueResponse;
import kr.hhplus.be.server.domain.token.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController implements SwaggerTokenController {

    private final QueueService queueService;

    @GetMapping("/waitingQueue/{userId}/{concertId}")
    public ResponseEntity<WaitingQueueResponse> waitingQueue(
            @PathVariable Long userId,
            @PathVariable Long concertId
    ){
        Long rank = queueService.addWaitingQueue(String.valueOf(userId),String.valueOf(concertId));
        return ResponseEntity.ok(WaitingQueueResponse.of(userId, rank));
    }

}
