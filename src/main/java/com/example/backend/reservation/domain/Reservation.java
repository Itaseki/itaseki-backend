package com.example.backend.reservation.domain;

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
@Table(name = "video_reservation")
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private Integer startHour;

    @Column(nullable = false)
    private Integer startMin;

    @Column(nullable = false)
    private Integer endHour;

    @Column(nullable = false)
    private Integer endMin;

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
    public Reservation(LocalDate date,Integer sHour, Integer sMin, Integer eHour, Integer eMin, User user, Video video){
        this.createdTime=LocalDateTime.now();
        this.reservationDate=date;
        this.startHour=sHour;
        this.startMin=sMin;
        this.endHour=eHour;
        this.endMin=eMin;
        this.user=user;
        this.video=video;
    }
}
