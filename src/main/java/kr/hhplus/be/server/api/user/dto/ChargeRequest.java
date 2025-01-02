package kr.hhplus.be.server.api.user.dto;

import lombok.Getter;

@Getter
public class ChargeRequest {
    private String user_uuid;
    private Long amount;
}
