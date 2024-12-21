package com.our.ourroom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public class CustomException extends RuntimeException {
    private final String error;

    public CustomException(String error, String details) {
        super(details); // 부모 클래스에 메시지 설정
        this.error = error; // error 메시지 설정
    }

    public String getError() {
        return error;
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getError()); // 예외에서 제공된 error 메시지
        body.put("details", ex.getMessage()); // 예외에서 제공된 details 메시지
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body); // 상태 코드 설정
    }
}