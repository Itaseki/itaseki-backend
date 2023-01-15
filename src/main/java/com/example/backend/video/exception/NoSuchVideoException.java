package com.example.backend.video.exception;

public class NoSuchVideoException extends RuntimeException {
    private static final String MESSAGE = "잘못된 비디오에 대한 요청";

    public NoSuchVideoException() {
        super(MESSAGE);
    }
}
