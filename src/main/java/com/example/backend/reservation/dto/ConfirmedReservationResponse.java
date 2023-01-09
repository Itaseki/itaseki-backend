package com.example.backend.reservation.dto;

import com.example.backend.reservation.converter.TimeConverter;
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

    public static ConfirmedReservationResponse of(ConfirmedReservation reservation){
        Video video = reservation.getVideo();
        return ConfirmedReservationResponse.builder()
                .reservationId(reservation.getId())
                .videoId(video.getId())
                .title(video.getDescription())
                .reservationDate(reservation.getReservationDate().toString())
                .startTime(TimeConverter.convertToString(reservation.getStartTime()))
                .endTime(TimeConverter.convertToString(reservation.getEndTime()))
                .build();
    }
}
