package kr.hhplus.be.server.application.dto;

public record PaymentReservationInfo(long concertScheduleId, long userId, long seatId, long paymentId, long amount ){ }

