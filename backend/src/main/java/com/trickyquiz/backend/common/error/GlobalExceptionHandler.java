package com.trickyquiz.backend.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// Controller에서 발생한 예외를 한 곳에서 JSON 응답으로 바꾸는 전역 예외 처리기입니다.
public class GlobalExceptionHandler {

    // @Valid 검증 실패처럼 요청 형식이 맞지 않는 경우를 처리합니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException() {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("요청 값이 올바르지 않습니다."));
    }

    // 지원하지 않는 카테고리처럼 비즈니스 규칙에 맞지 않는 요청을 처리합니다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(exception.getMessage()));
    }

    // 서비스 계층에서 상태 코드를 직접 지정한 경우를 그대로 응답합니다.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
        String message = exception.getReason();
        if (message == null || message.isBlank()) {
            message = "요청을 처리할 수 없습니다.";
        }

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ErrorResponse(message));
    }

    // 예상하지 못한 예외가 사용자에게 상세히 노출되지 않도록 공통 메시지로 처리합니다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버 내부 오류가 발생했습니다."));
    }
}
