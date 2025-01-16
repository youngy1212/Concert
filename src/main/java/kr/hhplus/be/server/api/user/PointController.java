package kr.hhplus.be.server.api.user;


import kr.hhplus.be.server.api.user.dto.ChargeRequest;
import kr.hhplus.be.server.api.user.dto.ChargeResponse;
import kr.hhplus.be.server.api.user.dto.PointResponse;
import kr.hhplus.be.server.api.user.dto.SwaggerPointController;
import kr.hhplus.be.server.domain.user.service.UserCommandService;
import kr.hhplus.be.server.domain.user.service.UserQueryService;
import kr.hhplus.be.server.domain.user.service.dto.ChargeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController implements SwaggerPointController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @PostMapping("/point/charge")
    public ResponseEntity<ChargeResponse> chargePoint(@RequestBody ChargeRequest request){
        request.validateAmount();
        ChargeDto chargeDto = userCommandService.chargePoint(request.getUserId(),
                request.getAmount());
        return ResponseEntity.ok(ChargeResponse.of(chargeDto.userId(), chargeDto.Amount()));
    }

    @GetMapping("/point/{userId}")
    public ResponseEntity<PointResponse> getPoint(
            @PathVariable Long userId
    ){
        ChargeDto chargeDto = userQueryService.getPoint(userId);
        return ResponseEntity.ok(PointResponse.of(chargeDto.userId(), chargeDto.Amount()));
    }

}
