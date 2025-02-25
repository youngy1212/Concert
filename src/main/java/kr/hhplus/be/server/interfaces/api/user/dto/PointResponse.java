package kr.hhplus.be.server.interfaces.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PointResponse {

    @Schema(description = "유저 Id")
    private final Long userId;

    @Schema(description = "잔액")
    private final Long totalAmount;


    private PointResponse(Long userId, Long totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;
    }

    public static PointResponse of(Long userId, Long totalAmount) {
        return new PointResponse(userId, totalAmount);
    }
}
