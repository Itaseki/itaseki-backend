package com.example.backend.reservation.domain;

import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@RequiredArgsConstructor
public class ConfirmedReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirm_id")
    private Long id;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate reservationDate;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    public ConfirmedReservation(Reservation reservation){
        this.reservationDate=reservation.getReservationDate();
        this.startTime=reservation.getStartTime();
        this.endTime=reservation.getEndTime();
        this.video=reservation.getVideo();
    }



}
