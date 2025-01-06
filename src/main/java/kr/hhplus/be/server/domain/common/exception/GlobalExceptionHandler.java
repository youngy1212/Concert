package kr.hhplus.be.server.domain.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        HttpStatus status = ex.getHttpStatus();

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
