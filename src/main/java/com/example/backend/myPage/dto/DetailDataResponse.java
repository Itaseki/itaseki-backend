package com.example.backend.myPage.dto;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailDataResponse {
    private final Long id;
    private final String title;
    private final int likeCount;
    private final String thumbnailUrl;
    private final Boolean isPublic;

    public static DetailDataResponse forMyPlaylist(Playlist playlist, String thumbnail) {
        return DetailDataResponse.playlistBuilder()
                .playlist(playlist)
                .thumbnailUrl(thumbnail)
                .isPublic(playlist.getIsPublic())
                .build();
    }

    public static DetailDataResponse forSavedPlaylist(Playlist playlist, String thumbnail) {
        return DetailDataResponse.playlistBuilder()
                .playlist(playlist)
                .thumbnailUrl(thumbnail)
                .isPublic(null)
                .build();
    }

    public static DetailDataResponse forMyVideo(Video video) {
        return DetailDataResponse.videoBuilder()
                .video(video)
                .build();
    }

    @Builder(builderClassName = "playlistBuilder", builderMethodName = "playlistBuilder")
    private DetailDataResponse(Playlist playlist, String thumbnailUrl, Boolean isPublic) {
        this.id = playlist.getId();
        this.title = playlist.getTitle();
        this.thumbnailUrl = thumbnailUrl;
        this.likeCount = playlist.getLikeCount();
        this.isPublic = isPublic;
    }

    @Builder(builderClassName = "videoBuilder", builderMethodName = "videoBuilder")
    private DetailDataResponse(Video video) {
        this.id = video.getId();
        this.title = video.getDescription();
        this.thumbnailUrl = video.getThumbnailUrl();
        this.likeCount = video.getLikeCount();
        this.isPublic = null;
    }
}
