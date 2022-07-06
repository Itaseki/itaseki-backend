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
                .runtime(getVideoRuntimeString(video))
                .build();
    }

    private static String getVideoRuntimeString(Video video){
        String h = video.getRuntimeHour()<10?"0"+video.getRuntimeHour():video.getRuntimeHour().toString();
        String m = video.getRuntimeMin()<10?"0"+video.getRuntimeMin():video.getRuntimeMin().toString();
        String s = video.getRuntimeSec()<10?"0"+video.getRuntimeSec():video.getRuntimeSec().toString();
        return h+":"+m+":"+s;
    }
}
