package com.example.backend.reservation.dto;

import com.example.backend.reservation.converter.TimeConverter;
import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BestReservationResponse {
    private Long id;
    private String title;
    private String runTime;
    private String videoUrl;
    private String thumbnailUrl;
    private Long count;
    private String reservationDate;
    private String startTime;
    private String endTime;

    public static BestReservationResponse fromDto(ReservationGroupDto dto){
        Video video = dto.getVideo();
        return BestReservationResponse.builder()
                .id(video.getId())
                .title(video.getDescription())
                .runTime(video.getConvertedRuntime())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .count(dto.getReservationCount())
                .reservationDate(dto.getReservationDate().toString())
                .startTime(TimeConverter.convertToString(dto.getStartTime()))
                .endTime(TimeConverter.convertToString(dto.getEndTime()))
                .build();
    }
}
