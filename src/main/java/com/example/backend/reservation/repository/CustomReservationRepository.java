package com.example.backend.reservation.repository;

import com.example.backend.reservation.domain.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface CustomReservationRepository {
    List<Reservation> getReservationsConfirmNeeded(LocalDate criteriaDate, Long confirmCriteria);
}
