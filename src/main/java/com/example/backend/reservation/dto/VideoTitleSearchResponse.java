package com.example.backend.reservation.dto;

import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VideoTitleSearchResponse {
    private Long id;
    private String title;
    private String url;
    private Integer runtimeHour;
    private Integer runtimeMin;
    private Integer runtimeSec;

    public static VideoTitleSearchResponse fromEntity(Video video){
        return VideoTitleSearchResponse.builder()
                .id(video.getId())
                .title(video.getDescription())
                .url(video.getVideoUrl())
                .runtimeHour(video.getRuntimeHour())
                .runtimeMin(video.getRuntimeMin())
                .runtimeSec(video.getRuntimeSec())
                .build();
    }


}
