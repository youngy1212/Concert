package kr.hhplus.be.server.interfaces.api.token.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class TokenResponse {

    @Schema(description = "토큰 UUID")
    private String tokenId;

    @Schema(description = "토큰 만료 시간")
    private LocalDateTime expiresAt;


    private TokenResponse(String tokenId, LocalDateTime expiresAt) {
        this.tokenId = tokenId;
        this.expiresAt = expiresAt;
    }

    public static TokenResponse of(String tokenId, LocalDateTime expiresAt) {
        return new TokenResponse(tokenId,expiresAt);
    }

}
