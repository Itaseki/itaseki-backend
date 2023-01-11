package com.example.backend.myPage.dto;

import com.example.backend.video.domain.Video;
import lombok.Getter;

@Getter
public class VideoResponseDto {
    private final Long id;
    private final String title;
    private final String thumbnailUrl;
    private final String videoUrl;

    private VideoResponseDto(Video video) {
        this.id = video.getId();
        this.title = video.getOriginVideoTitle();
        this.thumbnailUrl = video.getThumbnailUrl();
        this.videoUrl = video.getVideoUrl();
    }

    public static VideoResponseDto of(Video video) {
        return new VideoResponseDto(video);
    }
}
