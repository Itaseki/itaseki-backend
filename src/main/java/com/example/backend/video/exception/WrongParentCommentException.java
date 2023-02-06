package com.example.backend.video.exception;

public class WrongParentCommentException extends RuntimeException {
    private static final String MESSAGE = "잘못된 부모 댓글 ID 입니다.";

    public WrongParentCommentException() {
        super(MESSAGE);
    }
}
