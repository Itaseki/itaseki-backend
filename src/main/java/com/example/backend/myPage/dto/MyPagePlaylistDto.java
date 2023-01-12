package com.example.backend.myPage.dto;

import com.example.backend.playlist.domain.Playlist;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPagePlaylistDto {
    private final Long id;
    private final String title;
    private final String titleImageUrl;
    private final String writerNickname;
    private final int likeCount;
    private final int videoCount;
    private final Boolean isPublic;

    public static MyPagePlaylistDto forMyPlaylist(Playlist playlist, String thumbnail, int videoCount) {
        return MyPagePlaylistDto.builder()
                .playlist(playlist)
                .firstThumbnail(thumbnail)
                .videoCount(videoCount)
                .isPublic(playlist.getIsPublic())
                .build();
    }

    public static MyPagePlaylistDto forSavedPlaylist(Playlist playlist, String thumbnail, int videoCount) {
        return MyPagePlaylistDto.builder()
                .playlist(playlist)
                .firstThumbnail(thumbnail)
                .videoCount(videoCount)
                .isPublic(null)
                .build();
    }

    @Builder
    private MyPagePlaylistDto(Playlist playlist, String firstThumbnail, int videoCount, Boolean isPublic) {
        this.id = playlist.getId();
        this.title = playlist.getTitle();
        this.titleImageUrl = firstThumbnail;
        this.writerNickname = playlist.getUser().getNickname();
        this.likeCount = playlist.getLikeCount();
        this.videoCount = videoCount;
        this.isPublic = isPublic;
    }
}
