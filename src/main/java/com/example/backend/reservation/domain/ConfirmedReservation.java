package com.example.backend.reservation.domain;

import com.example.backend.reservation.dto.ReservationGroupDto;
import com.example.backend.video.domain.Video;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@RequiredArgsConstructor
public class ConfirmedReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirm_id")
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate reservationDate;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column
    private Long reservationCount;

    public static ConfirmedReservation fromDto(ReservationGroupDto dto) {
        return new ConfirmedReservation(dto.getReservationDate(), dto.getStartTime(), dto.getEndTime(), dto.getVideo(), dto.getReservationCount());
    }

    private ConfirmedReservation(LocalDate date, LocalDateTime startTime, LocalDateTime endTime, Video video, long count) {
        this.reservationDate = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.video = video;
        this.reservationCount = count;
    }


}
