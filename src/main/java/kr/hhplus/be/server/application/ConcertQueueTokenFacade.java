package kr.hhplus.be.server.application;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import kr.hhplus.be.server.application.dto.QueueTokenInfo;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.service.ConcertQueryService;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.service.QueueTokenCommandService;
import kr.hhplus.be.server.domain.token.service.QueueTokenQueryService;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertQueueTokenFacade {

    private final UserQueryService userQueryService;
    private final ConcertQueryService concertQueryService;
    private final QueueTokenQueryService queueTokenQueryService;
    private final QueueTokenCommandService queueTokenCommandService;

    //토큰 생성
    @Transactional
    public QueueTokenInfo issueQueueToken(long userId, long concertId) {

        User user = userQueryService.getUserById(userId);
        Concert concert = concertQueryService.getConcertById(concertId);

        // 이미 토큰이 있는지 확인
        Optional<QueueToken> findToken = queueTokenQueryService.findToken(user.getId(), concert.getId());

        if (findToken.isPresent()) { //토큰이 있을때
            QueueToken token = findToken.get();
            token.tokenActive(LocalDateTime.now()); //현재 시간으로 다시 활성화
            return new QueueTokenInfo(token.getQueueTokenId(), token.getUser().getId(), token.getConcert().getId(), token.getExpiresAt());
        }
        //없을때
        QueueToken queueToken = queueTokenCommandService.issueToken(user, concert);
        return  new QueueTokenInfo(queueToken.getQueueTokenId(), queueToken.getUser().getId(), queueToken.getConcert().getId(), queueToken.getExpiresAt());

    }



}
