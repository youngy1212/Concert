package kr.hhplus.be.server.api.reservation.dto;

import lombok.Getter;

@Getter
public class ReservationRequest {
    private String user_uuid;
    private Long seat_id;
    private String token_uuid;
}
