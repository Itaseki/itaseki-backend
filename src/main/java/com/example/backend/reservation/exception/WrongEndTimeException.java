package com.example.backend.reservation.exception;

public class WrongEndTimeException extends RuntimeException {
    private static final String MESSAGE = "잘못된 예약 종료 시간";

    public WrongEndTimeException() {
        super(MESSAGE);
    }
}
