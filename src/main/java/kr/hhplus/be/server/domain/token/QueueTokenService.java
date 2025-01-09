package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueTokenService {

    private final QueueTokenStore queueTokenStore;
    private final QueueTokenReader queueTokenReader;

    public QueueToken issueToken(final User user, final Concert concert) {

        QueueToken queueToken = QueueToken.create(user, concert);
        queueTokenStore.save(queueToken);
        return queueToken;
    }

    public QueueToken authenticateToken(String token) {

        QueueToken queueToken =  queueTokenReader.tokenFindById(token).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,"잘못된 경로로 접근하였습니다."));

        if(queueToken.isExpired()){
            return queueToken;
        }else {
            throw new CustomException("대기시간이 만료되었습니다.");
        }

    }
}
