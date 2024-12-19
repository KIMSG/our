package com.our.ourroom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler
 * - 애플리케이션 전역에서 발생하는 예외를 처리합니다.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 500 Internal Server Error 예외 처리
     * @param ex 발생한 예외
     * @param request 요청 정보
     * @return 사용자에게 반환할 오류 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalServerError(Exception ex, WebRequest request) {
        // 응답 본문 데이터 구성
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "Internal server error");
        body.put("details", "서버에서 예기치 않은 오류가 발생했습니다. 나중에 다시 시도해 주세요.");

        // 디버깅용 추가 정보(선택 사항)
        // body.put("exception", ex.getClass().getName());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}