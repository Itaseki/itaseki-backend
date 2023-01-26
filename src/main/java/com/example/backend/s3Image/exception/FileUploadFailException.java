package com.example.backend.s3Image.exception;

public class FileUploadFailException extends RuntimeException {
    private static final String MESSAGE = "파일 업로드 실패";

    public FileUploadFailException() {
        super(MESSAGE);
    }
}
