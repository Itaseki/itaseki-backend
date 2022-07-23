package com.example.backend.reservation.dto;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.reservation.domain.Reservation;
import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DetailReservationResponse {
    public Long reservationId;
    public Long videoId;
    public String description;
    public String thumbnailUrl;
    public String url;
    public String writerNickname;
    public Integer count;

    public static DetailReservationResponse fromConfirm(ConfirmedReservation res, int count){
        Video video = res.getVideo();
        return DetailReservationResponse.builder()
                .reservationId(res.getId())
                .videoId(video.getId())
                .description(video.getDescription())
                .thumbnailUrl(video.getThumbnailUrl())
                .url(video.getVideoUrl())
                .writerNickname(video.getUser().getNickname())
                .count(count)
                .build();
    }

    public static DetailReservationResponse fromReservation(Reservation res, int count){
        Video video = res.getVideo();
        return DetailReservationResponse.builder()
                .reservationId(res.getId())
                .videoId(video.getId())
                .description(video.getDescription())
                .thumbnailUrl(video.getThumbnailUrl())
                .url(video.getVideoUrl())
                .writerNickname(video.getUser().getNickname())
                .count(count)
                .build();
    }
}
