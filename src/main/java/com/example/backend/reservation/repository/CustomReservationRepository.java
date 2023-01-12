package com.example.backend.reservation.repository;

import com.example.backend.reservation.dto.ReservationGroupDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomReservationRepository {
    List<ReservationGroupDto> findReservationsConfirmNeeded(LocalDate criteriaDate, Long confirmCriteria);

    List<ReservationGroupDto> findReservationByDate(LocalDate date);

    List<ReservationGroupDto> findAllByTimeCondition(LocalDateTime startTime, LocalDateTime endTime,
                                                     List<LocalDateTime> selection);
}
