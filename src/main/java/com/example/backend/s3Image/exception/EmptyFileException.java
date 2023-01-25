package com.example.backend.s3Image.exception;

public class EmptyFileException extends RuntimeException {
    private static final String MESSAGE = "빈 파일입니다.";

    public EmptyFileException() {
        super(MESSAGE);
    }
}
