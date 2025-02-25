package kr.hhplus.be.server.interfaces.api.token.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class WaitingQueueResponse {

    @Schema(description = "유저 ID ")
    private long userId;

    @Schema(description = "대기 순서")
    private long rank;

    private WaitingQueueResponse(long userId, long rank) {
        this.userId = userId;
        this.rank = rank;
    }

    public static WaitingQueueResponse of(long userId, long rank) {
        return new WaitingQueueResponse(userId,rank);
    }


}
