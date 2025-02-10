package kr.hhplus.be.server.domain.token.service;

import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.repository.QueueTokenCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueTokenCommandService {

    private final QueueTokenCommand queueTokenCommand;

    public QueueToken issueToken(final Long userId, final Long concerId) {

        QueueToken queueToken = QueueToken.create(userId, concerId);
        queueTokenCommand.save(queueToken);
        return queueToken;
    }



}
