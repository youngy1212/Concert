package kr.hhplus.be.server.api.reservation.dto;

import lombok.Getter;

@Getter
public class ReservationResponse {
    private final Long payment_id;
    private final Long reservation_id;

    private ReservationResponse(Long payment_id, Long reservation_id) {
        this.payment_id = payment_id;
        this.reservation_id = reservation_id;
    }

    public static ReservationResponse of(Long payment_id, Long reservation_id) {
      return new ReservationResponse(payment_id, reservation_id);
    }
}
