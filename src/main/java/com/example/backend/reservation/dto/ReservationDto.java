package com.example.backend.reservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationDto {
    private Long id;
    private LocalDate reservationDate;
    private Integer startTime;
    private Integer startMin;
    private Integer endTime;
    private Integer endMin;
}
