package com.example.backend.reservation.repository;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.video.domain.Video;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConfirmedReservationRepository extends JpaRepository<ConfirmedReservation, Long> {
    List<ConfirmedReservation> findAllByReservationDate(LocalDate date);
    Optional<ConfirmedReservation> findByStartTimeAndEndTimeAndVideo(LocalDateTime startTime, LocalDateTime endTime, Video video);
    List<ConfirmedReservation> findByStartTimeGreaterThanEqualOrderByStartTime(LocalDateTime now);
    Optional<ConfirmedReservation> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(LocalDateTime now1, LocalDateTime now);
}
