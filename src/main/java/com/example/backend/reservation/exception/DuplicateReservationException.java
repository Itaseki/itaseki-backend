package com.example.backend.reservation.exception;

public class DuplicateReservationException extends RuntimeException {
    private static final String MESSAGE = "중복 예약 불가";

    public DuplicateReservationException() {
        super(MESSAGE);
    }

}
