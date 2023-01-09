package com.example.backend.reservation.exception;

public class ReservationTimeConflictException extends RuntimeException {
    private static final String MESSAGE = "선택 불가능한 예약시간";

    public ReservationTimeConflictException() {
        super(MESSAGE);
    }
}
