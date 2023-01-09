package com.example.backend.reservation.exception;

public class ConfirmExistException extends RuntimeException {
    private static final String MESSAGE = "이미 해당 시간에 예약이 확정되어 있는 영상";

    public ConfirmExistException() {
        super(MESSAGE);
    }
}
