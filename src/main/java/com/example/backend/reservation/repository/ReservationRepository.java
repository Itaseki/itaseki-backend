package com.example.backend.reservation.repository;

import com.example.backend.reservation.domain.Reservation;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>,CustomReservationRepository {
    Optional<Reservation> findByReservationDateAndUser(LocalDate date, User user);
    List<Reservation> findAllByReservationDateAndStartTimeAndEndTimeAndVideo(LocalDate date, String startTime, String endTime, Video video);
}
