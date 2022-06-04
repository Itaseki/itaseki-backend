package com.example.backend.reservation.repository;

import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.dto.ReservationCountDto;

import java.time.LocalDate;
import java.util.List;

public interface CustomReservationRepository {
    List<ReservationCountDto> getReservationsConfirmNeeded(LocalDate criteriaDate, Long confirmCriteria);
    List<ReservationCountDto> getDateReservationGroupVideo(LocalDate date);
}
