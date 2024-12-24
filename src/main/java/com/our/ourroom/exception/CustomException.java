package com.our.ourroom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * CustomException
 * 애플리케이션에서 사용자 정의 예외를 처리하기 위한 클래스입니다.
 * 에러 메시지와 상세 내용을 포함합니다.
 */
public class CustomException extends RuntimeException {
    private final String error;

    /**
     * CustomException 생성자
     * @param error 에러 유형
     * @param details 상세 에러 메시지
     */
    public CustomException(String error, String details) {
        super(details); // 부모 클래스에 메시지 설정
        this.error = error; // error 메시지 설정
    }

    /**
     * 에러 유형 반환
     * @return 에러 유형 문자열
     */
    public String getError() {
        return error;
    }
}