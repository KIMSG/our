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
}