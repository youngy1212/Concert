package kr.hhplus.be.server.domain.user.service;

import jakarta.persistence.OptimisticLockException;
import java.util.NoSuchElementException;
import kr.hhplus.be.server.domain.common.exception.CustomException;
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

        try {

        Point point = userCommand.charge(userId).orElseThrow(() -> new NoSuchElementException("포인트를 찾을 수 없습니다."));
        point.charge(amount);

        return new ChargeDto(point.getUser().getId(), point.getAmount());
        } catch (OptimisticLockException e) {
            // 낙관적 락 충돌 처리
            throw new CustomException("포인트 충전 중 충돌이 발생했습니다. 다시 시도해주세요.");
        }

    }
}
