package kr.hhplus.be.server.domain.token;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.common.exception.CustomException;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.model.QueueTokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueueTokenTest {

    @DisplayName("PENDING인 토큰을 호출하여 ACTIVE 변경한다. expiresAt가 10분뒤로 설정되는지 확인한다.")
    @Test
    void testTokenActive() {
        // given'
        long userId = 1L;
        long concertId = 2L;
        QueueToken queueToken = QueueToken.create(userId, concertId);
        LocalDateTime now = LocalDateTime.now();

        // when
        queueToken.tokenActive(now);

        // then
        assertEquals(QueueTokenStatus.ACTIVE, queueToken.getStatus());
        assertEquals(now.plusMinutes(10), queueToken.getExpiresAt());

    }

    @DisplayName("이미 상태가 ACTIVE인 토큰을 ACTIVE로 변경 할 경우 예약중이라는 오류 발생")
    @Test
    void testTokenActive_AlreadyActive() {
        // given
        long userId = 1L;
        long concertId = 2L;
        QueueToken queueToken = QueueToken.create( userId, concertId);
        LocalDateTime now = LocalDateTime.now();
        queueToken.tokenActive(now);

        // when // then
        assertThatThrownBy(()->queueToken.tokenActive(now))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 예약중입니다.");
    }

    @DisplayName("유효시간이 현재시간보다 이전일 경우 ture 반환한다.")
    @Test
    void testIsExpired_BeforeNow() {
        // given
        long userId = 1L;
        long concertId = 2L;
        QueueToken queueToken = QueueToken.create( userId, concertId);
        LocalDateTime now = LocalDateTime.now().minusMonths(10);
        queueToken.tokenActive(now);

        // when
        boolean expired = queueToken.isExpired();

        // then
        assertTrue(expired);
    }

    @DisplayName("유효시간이 현재시간보다 이후 경우 false 반환한다.")
    @Test
    void testIsExpired_afterNow() {
        // given
        long userId = 1L;
        long concertId = 2L;
        QueueToken queueToken = QueueToken.create( userId, concertId);
        LocalDateTime now = LocalDateTime.now();
        queueToken.tokenActive(now);

        // when
        boolean expired = queueToken.isExpired();

        // then
        assertFalse(expired);
    }



}