package kr.hhplus.be.server.interfaces.api.concert.dto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.hhplus.be.server.domain.common.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


@Tag(name = "Concert Controller", description = "콘서트 관련 조회 API")
public interface SwaggerConcertController {

    @Operation(summary = "예약 가능 날짜 조회 API", description = "예약 가능한 날짜를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(array = @ArraySchema(
                                    schema = @Schema(implementation = ConcertDateResponse.class)
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "예약 가능한 날짜가 없습니다.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
    },security = @SecurityRequirement(name = "Authorization"))
    ResponseEntity<List<ConcertDateResponse>> getConcertDates(@PathVariable Long concertId);

    @Operation(
            summary = "예약 가능 좌석 조회",
            description = "예약 가능한 좌석을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = SeatResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "콘서트의 좌석을 찾을 수 없습니다.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            },
            security = @SecurityRequirement(name = "Authorization")
    )
    ResponseEntity<SeatResponse> getConcertSeats(@PathVariable Long concertScheduleId);

}

