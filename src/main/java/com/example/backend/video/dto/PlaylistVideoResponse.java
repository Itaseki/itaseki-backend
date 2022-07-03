package com.example.backend.video.dto;

import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaylistVideoResponse {
    private Long id;
    private String title;
    private String videoUploader;
    private String thumbnailUrl;
    private String runtime;

    public static PlaylistVideoResponse fromEntity(Video video){
        return PlaylistVideoResponse.builder()
                .id(video.getId())
                .title(video.getOriginVideoTitle())
                .videoUploader(video.getVideoUploader())
                .thumbnailUrl(video.getThumbnailUrl())
                .runtime(video.getRuntimeHour()+":"+video.getRuntimeMin()+":"+video.getRuntimeSec())
                .build();
    }
}
