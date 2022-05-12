package com.example.backend.video.dto;

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
}
