package kr.hhplus.be.server.domain.user.service;

import java.util.NoSuchElementException;
import kr.hhplus.be.server.domain.user.model.Point;
import kr.hhplus.be.server.domain.user.repository.UserCommand;
import kr.hhplus.be.server.domain.user.service.dto.ChargeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserCommand userCommand;

    @Transactional
    public ChargeDto chargePoint(Long userId, Long amount) {

        Point point = userCommand.charge(userId).orElseThrow(() -> new NoSuchElementException("포인트를 찾을 수 없습니다."));
        point.charge(amount);

        return new ChargeDto(point.getUser().getId(), point.getAmount());

    }
}
