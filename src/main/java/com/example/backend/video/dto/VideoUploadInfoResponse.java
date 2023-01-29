package com.example.backend.video.dto;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.video.domain.Hashtag;
import com.example.backend.video.domain.Series;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VideoUploadInfoResponse {
    private List<NameIdResponse> series;
    private List<NameIdResponse> hashtags;
    private List<NameIdResponse> playlists;

    public static VideoUploadInfoResponse toInfoResponse(List<Series> series, List<Hashtag> hashtags, List<Playlist> playlists){
        return VideoUploadInfoResponse.builder()
                .series(series.stream()
                        .map(NameIdResponse::new).collect(Collectors.toList()))
                .hashtags(hashtags.stream()
                        .map(NameIdResponse::new).collect(Collectors.toList()))
                .playlists(playlists.stream()
                        .map(NameIdResponse::new).collect(Collectors.toList()))
                .build();

    }
}
