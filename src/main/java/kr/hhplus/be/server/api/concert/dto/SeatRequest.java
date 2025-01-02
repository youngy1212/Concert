package kr.hhplus.be.server.api.concert.dto;

import lombok.Getter;

@Getter
public class SeatRequest {
    private Long concert_id;
    private String token_uuid;
}
