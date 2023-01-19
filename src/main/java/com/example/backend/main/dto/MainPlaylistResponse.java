package com.example.backend.main.dto;

import com.example.backend.playlist.domain.PlaylistVideo;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.video.domain.Video;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MainPlaylistResponse {
    private final long id;
    private final String titleImageUrl;
    private final List<String> videos;

    @Builder
    public MainPlaylistResponse(AllPlaylistsResponse playlist, String titleImage, List<PlaylistVideo> videos) {
        this.id = playlist.getId();
        this.titleImageUrl = titleImage;
        this.videos = videos.stream()
                .map(PlaylistVideo::getVideo)
                .map(Video::getDescription)
                .collect(Collectors.toList());
    }
}
