package com.example.backend.reservation.dto;

import com.example.backend.reservation.domain.Reservation;
import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BestReservationResponse {
    private Long id;
    private String title;
    private String runTime;
    private String videoUrl;
    private Long count;
    private String reservationDate;
    private String startTime;
    private String endTime;

    public static BestReservationResponse of(Reservation reservation, Long count){
        Video video = reservation.getVideo();
        return BestReservationResponse.builder()
                .id(video.getId()).title(video.getDescription())
                .runTime(video.getRuntimeHour()+":"+video.getRuntimeMin()+":"+video.getRuntimeSec())
                .videoUrl(video.getVideoUrl())
                .count(count)
                .reservationDate(reservation.getReservationDate().toString())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .build();
    }
}