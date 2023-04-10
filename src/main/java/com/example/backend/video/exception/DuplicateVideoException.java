package com.example.backend.video.exception;

public class DuplicateVideoException extends RuntimeException {
    private static final String MESSAGE = "이미 등록된 영상입니다.";

    public DuplicateVideoException() {
        super(MESSAGE);
    }
}
