package com.example.backend.reservation.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
public class ReservationDto {
    private final DateTimeFormatter FORMAT_WITH_COLON = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm");
    private Long id;
    private String reservationDate;
    private String startTime;
    private String endTime;

    public LocalDateTime getStartTime() {
        return LocalDateTime.parse(reservationDate + startTime, FORMAT_WITH_COLON);
    }

    public LocalDateTime getEndTime() {
        return LocalDateTime.parse(reservationDate + endTime, FORMAT_WITH_COLON);
    }

    public LocalDate getReservationDate() {
        return LocalDate.parse(reservationDate);
    }

    public Long getVideoId() {
        return id;
    }
}
