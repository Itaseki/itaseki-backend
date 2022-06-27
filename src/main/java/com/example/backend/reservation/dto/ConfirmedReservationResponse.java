package com.example.backend.reservation.dto;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConfirmedReservationResponse {
    private Long reservationId;
    private Long videoId;
    private String title;
    private String reservationDate;
    private String startTime;
    private String endTime;

    public static ConfirmedReservationResponse of(ConfirmedReservation res){
        Video video = res.getVideo();
        return ConfirmedReservationResponse.builder()
                .reservationId(res.getId())
                .videoId(video.getId())
                .title(video.getDescription())
                .reservationDate(res.getReservationDate().toString())
                .startTime(res.getStartTime())
                .endTime(res.getEndTime())
                .build();
    }
}
