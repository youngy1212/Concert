package kr.hhplus.be.server.api.user.dto;

import kr.hhplus.be.server.domain.common.exception.CustomException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChargeRequest {
    private Long userId;
    private Long amount;

    @Builder
    private ChargeRequest(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public void validateAmount() {
        if (amount == null || amount <= 0) {
            throw new CustomException("amount는 0보다 큰 양수여야 합니다.");
        }
    }

}
