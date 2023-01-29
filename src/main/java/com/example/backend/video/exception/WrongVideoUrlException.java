package com.example.backend.video.exception;

public class WrongVideoUrlException extends RuntimeException {
    private static final String MESSAGE = "잘못된 형식의 URL 입니다.";

    public WrongVideoUrlException() {
        super(MESSAGE);
    }
}
