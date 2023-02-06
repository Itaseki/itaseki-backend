package com.example.backend.video.exception;

public class NoSuchHashtagException extends RuntimeException {
    private final static String MESSAGE = "존재하지 않는 해시태그 ID 입니다.";

    public NoSuchHashtagException() {
        super(MESSAGE);
    }
}
