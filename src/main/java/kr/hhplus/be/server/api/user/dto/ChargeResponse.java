package kr.hhplus.be.server.api.user.dto;

import lombok.Getter;

@Getter
public class ChargeResponse {
    private final String user_uuid;
    private final Long amount;

    private ChargeResponse(String user_uuid, Long amount) {
        this.user_uuid = user_uuid;
        this.amount = amount;
    }

    public static ChargeResponse of(String user_uuid, Long amount) {
        return new ChargeResponse(user_uuid, amount);
    }
}
