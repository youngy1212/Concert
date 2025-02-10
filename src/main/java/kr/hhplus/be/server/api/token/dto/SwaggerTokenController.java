package kr.hhplus.be.server.api.token.dto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.common.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


@Tag(name = "Queue Controller", description = "대기열 토큰 발급 API")
public interface SwaggerTokenController {

    @Operation(
            summary = "대기열 토큰 발급",
            description = "유저의 대기열 토큰을 발급합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "발급 성공",
                            content = @Content(
                                    schema = @Schema(implementation = WaitingQueueResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "유저를 찾을 수 없습니다.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<WaitingQueueResponse> waitingQueue(@PathVariable Long userId,
                                               @PathVariable Long concertId);

}

