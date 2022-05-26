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

    @Builder
    public ConfirmedReservation(LocalDate date, String startTime, String endTime,Video video){
        this.reservationDate=date;
        this.startTime=startTime;
        this.endTime=endTime;
        this.video=video;
    }

}
