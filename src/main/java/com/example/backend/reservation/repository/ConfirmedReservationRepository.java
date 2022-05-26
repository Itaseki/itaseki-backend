package com.example.backend.reservation.repository;

import com.example.backend.reservation.domain.ConfirmedReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmedReservationRepository extends JpaRepository<ConfirmedReservation, Long> {
}
