package kr.hhplus.be.server.api.token;

import java.time.LocalDateTime;
import kr.hhplus.be.server.api.ApiResponse;
import kr.hhplus.be.server.api.token.dto.TokenRequest;
import kr.hhplus.be.server.api.token.dto.TokenResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @PostMapping("/tokens")
    public ApiResponse<TokenResponse> Servicetoken(@RequestBody TokenRequest request){
        return ApiResponse.ok(TokenResponse.of("uuid", LocalDateTime.now(),true));
    }

}
