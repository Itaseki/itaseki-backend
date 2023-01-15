package com.example.backend.video.dto;

import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Series;
import com.example.backend.video.domain.Video;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VideoDto {
    private String url;
    private String title;
    private String runtime;
    private String description;
    private Long series;
    private Integer episode;
    private List<Long> hashtags;
    private List<String> keywords;
    private List<Long> playlists;
    private String thumbnailUrl;
    private String videoUploader;

    public Video toEntityWithUserAndSeries(User user, Series series) {
        return Video.builder()
                .videoUrl(url)
                .description(description)
                .originTitle(title)
                .episodeNumber(episode)
                .user(user)
                .series(series)
                .thumbnailUrl(thumbnailUrl)
                .uploader(videoUploader)
                .build();
    }
}
