package com.example.backend.video.exception;

public class NoSuchSeriesException extends RuntimeException {
    private static final String MESSAGE = "잘못된 시리즈 번호 입니다.";

    public NoSuchSeriesException() {
        super(MESSAGE);
    }
}
