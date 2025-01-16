package kr.hhplus.be.server.api.reservation.dto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.common.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Reservation Controller", description = "콘서트 예약 결제 API")
public interface SwaggerReservationController {

    @Operation(summary = "좌석 예약 요청 API", description = "콘서트와 좌석을 선택하여 예약합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "임시 좌석 예약 성공",
                            content = @Content(array = @ArraySchema(
                                    schema = @Schema(implementation = ReservationResponse.class)
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "예약 가능한 날짜가 없습니다.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "이미 선택된 좌석입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
            },security = @SecurityRequirement(name = "Authorization"))
    ResponseEntity<ReservationResponse> reservationSeat(@RequestBody ReservationRequest request);


    @Operation(
            summary = "결제 요청",
            description = "예약에 대한 결제를 진행합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = CompleteReservationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "예약 정보가 일치하지 않습니다.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "402",
                            description = "결제에 실패하였습니다.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
            },
            security = @SecurityRequirement(name = "Authorization")
    )
    ResponseEntity<CompleteReservationResponse> reservationSeat(@RequestBody PaymentReservationRequest request);

}

