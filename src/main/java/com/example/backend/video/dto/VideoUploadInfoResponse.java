package com.example.backend.video.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class VideoUploadInfoResponse {
    private List<InnerInfoResponse> series;
    private List<InnerInfoResponse> hashtags;
    private List<InnerInfoResponse> playlists;

    public static VideoUploadInfoResponse toInfoResponse(List<InnerInfoResponse> series,List<InnerInfoResponse> hashtags, List<InnerInfoResponse> playlists){
        return VideoUploadInfoResponse.builder()
                .series(series)
                .hashtags(hashtags)
                .playlists(playlists)
                .build();

    }
}
