package kr.hhplus.be.server.domain.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxStatus {

    INIT("메세지 전송 준비"),
    SUCCESS("메세지 전송 완료");

    private final String text;
}
