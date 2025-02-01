package kr.hhplus.be.server.infrastructure.gateway;

import kr.hhplus.be.server.domain.common.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PaySystem {

    public static void pay(Long amount) {
        //외부 시스템
    }

    public static boolean payFail(Long amount){ //실패한다고 가정
        throw new CustomException(HttpStatus.PAYMENT_REQUIRED, "결제에 실패하였습니다.");
    }
}
