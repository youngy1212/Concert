package kr.hhplus.be.server.interfaces.api.user.dto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.common.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Point Controller", description = "포인트 API")
public interface SwaggerPointController {

    @Operation(
            summary = "잔액 충전",
            description = "유저의 POINT를 충전합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "충전 성공",
                            content = @Content(
                                    schema = @Schema(implementation = ChargeResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "유저를 찾을 수 없습니다.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "잘못 청구되었습니다.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )

            }
    )
    ResponseEntity<ChargeResponse> chargePoint(@RequestBody ChargeRequest request);


    @Operation(
            summary = "잔액 조회",
            description = "유저의 POINT를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "유저의 POINT를 조회합니다.",
                            content = @Content(
                                    schema = @Schema(implementation = PointResponse.class)
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
    ResponseEntity<PointResponse> getPoint(@PathVariable Long userId);




}

