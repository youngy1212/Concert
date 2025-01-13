package kr.hhplus.be.server.application;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import kr.hhplus.be.server.api.token.dto.TokenResponse;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.service.QueueTokenService;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertQueueTokenFacade {

    private final UserService userService;
    private final ConcertService concertService;
    private final QueueTokenService queueTokenService;

    //토큰 생성
    @Transactional
    public TokenResponse issueQueueToken(long userId, long concertId) {

        User user = userService.getUserById(userId);
        Concert concert = concertService.getConcertById(concertId);

        // 이미 토큰이 있는지 확인
        Optional<QueueToken> findToken = queueTokenService.findByUserAndConcert(user.getId(), concert.getId());

        if (findToken.isPresent()) { //토큰이 있을때
            QueueToken token = findToken.get();
            token.tokenActive(LocalDateTime.now()); //현재 시간으로 다시 활성화
            return TokenResponse.of(token.getQueueTokenId(), token.getExpiresAt());
        }

        //없을때
        QueueToken queueToken = queueTokenService.issueToken(user, concert);
        return TokenResponse.of(queueToken.getQueueTokenId(), queueToken.getExpiresAt());

    }



}
