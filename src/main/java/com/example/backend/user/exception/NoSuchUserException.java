package com.example.backend.user.exception;

public class NoSuchUserException extends RuntimeException {
    private static final String MESSAGE = "잘못된 사용자에 대한 요청";

    public NoSuchUserException() {
        super(MESSAGE);
    }
}
