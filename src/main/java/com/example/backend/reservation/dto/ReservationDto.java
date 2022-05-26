package com.example.backend.reservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationDto {
    private Long id;
    private String reservationDate;
    private String startTime;
    private String endTime;
}
