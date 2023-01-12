package com.example.backend.reservation.domain;

import com.example.backend.reservation.dto.ReservationDto;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "run_reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate reservationDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false)
    private LocalDateTime endTime;

    @CreatedDate
    @Column
    private LocalDateTime createdTime;

    //1 user - N reservation
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //1 video - N reservation
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Builder
    private Reservation(LocalDate date, LocalDateTime startTime, LocalDateTime endTime, User user, Video video) {
        this.createdTime = LocalDateTime.now();
        this.reservationDate = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.video = video;
    }

    public static Reservation fromDtoAndUserVideo(ReservationDto dto, User user, Video video) {
        return Reservation.builder()
                .date(dto.getReservationDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .user(user)
                .video(video)
                .build();
    }
}
