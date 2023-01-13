package com.example.backend.user.exception;

public class WrongAuthorizationException extends RuntimeException {
    private static final String MESSAGE = "권한이 없는 요청입니다.";

    public WrongAuthorizationException() {
        super(MESSAGE);
    }
}
