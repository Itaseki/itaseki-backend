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
public class NextRunResponse {
    private Long reservationId;
    private Long videoId;
    private String title;
    private String runTime;
    private String thumbnailUrl;
    private String videoUrl;
    private Long count;
    private String reservationDate;
    private String startTime;

    public static NextRunResponse of(ConfirmedReservation res){
        Video video = res.getVideo();
        return NextRunResponse.builder()
                .reservationId(res.getId())
                .videoId(video.getId())
                .title(video.getDescription())
                .runTime(video.getConvertedRuntime())
                .thumbnailUrl(video.getThumbnailUrl())
                .videoUrl(video.getVideoUrl())
                .count(res.getReservationCount())
                .reservationDate(res.getReservationDate().toString())
                .startTime(TimeConverter.convertToString(res.getStartTime()))
                .build();
    }
}
