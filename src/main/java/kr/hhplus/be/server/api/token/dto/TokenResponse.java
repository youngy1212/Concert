package kr.hhplus.be.server.api.token.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class TokenResponse {

    private String token_uuid;
    private LocalDateTime expires_at;
    private boolean is_valid;

    private TokenResponse(String token_uuid, LocalDateTime expires_at, boolean is_valid) {
        this.token_uuid = token_uuid;
        this.expires_at = expires_at;
        this.is_valid = is_valid;
    }

    public static TokenResponse of(String token_uuid, LocalDateTime expires_at,boolean is_valid) {
        return new TokenResponse(token_uuid,expires_at,is_valid);
    }

}
