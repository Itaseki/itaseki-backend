package com.example.backend.myPage.dto;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistVideo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailPlaylistDto {
    private final Long id;
    private final String title;
    private final List<VideoResponseDto> videos;
    private final Boolean isPublic;

    @Builder
    public DetailPlaylistDto(Playlist playlist, List<PlaylistVideo> videos) {
        this.id = playlist.getId();
        this.title = playlist.getTitle();
        this.videos = mapVideoToResponse(videos);
        this.isPublic = playlist.getIsPublic();
    }

    private List<VideoResponseDto> mapVideoToResponse(List<PlaylistVideo> videos) {
        return videos.stream()
                .map(PlaylistVideo::getVideo)
                .map(VideoResponseDto::of)
                .collect(Collectors.toList());
    }
}
