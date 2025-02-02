package kr.hhplus.be.server.application.dto;

import java.time.LocalDateTime;

public record QueueTokenInfo(String queueTokenId, Long userId, Long concertId, LocalDateTime expiresAt) {}