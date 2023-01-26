package com.example.backend.globalexception;

public class WrongParamException extends RuntimeException {
    private static final String MESSAGE = "잘못된 파라미터";

    public WrongParamException() {
        super(MESSAGE);
    }
}
