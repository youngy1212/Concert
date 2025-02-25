package kr.hhplus.be.server.interfaces.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ChargeResponse {
    @Schema(description = "유저 UUID")
    private final Long userId;

    @Schema(example = "10000", description = "충전 금액")
    private final Long amount;

    private ChargeResponse(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public static ChargeResponse of(Long userId, Long amount) {
        return new ChargeResponse(userId, amount);
    }
}
