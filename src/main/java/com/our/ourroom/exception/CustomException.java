package com.our.ourroom.exception;

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