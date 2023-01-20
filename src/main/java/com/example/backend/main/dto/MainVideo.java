package com.example.backend.main.dto;

import com.example.backend.video.domain.Video;
import lombok.Getter;

@Getter
public class MainVideo {
    private final long id;
    private final String thumbnailUrl;

    private MainVideo(Video video) {
        this.id = video.getId();
        this.thumbnailUrl = video.getThumbnailUrl();
    }

    public static MainVideo ofVideo(Video video) {
        return new MainVideo(video);
    }
}
