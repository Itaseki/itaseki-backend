package com.example.backend.reservation.dto;

import com.example.backend.video.domain.Video;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationGroupDto {
    private Video video;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate reservationDate;
    private long reservationCount;
}
