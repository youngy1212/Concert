package kr.hhplus.be.server.domain.user.service;

import java.util.NoSuchElementException;
import kr.hhplus.be.server.domain.user.model.Point;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.repository.UserQuery;
import kr.hhplus.be.server.domain.user.service.dto.ChargeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserQuery userQuery;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userQuery.findById(userId)
                .orElseThrow(() -> new NoSuchElementException( "유저를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public ChargeInfo getPoint(Long userId) {
        Point point = userQuery.findByPoint(userId).orElseThrow(() -> new NoSuchElementException("포인트를 찾을 수 없습니다."));
        return new ChargeInfo(point.getUser().getId(), point.getAmount());
    }

}
