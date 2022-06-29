package com.example.backend.playlist.dto;

import com.example.backend.playlist.domain.UserSavedPlaylist;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaylistTitleResponse {
    private Long id;
    private String title;

    public static PlaylistTitleResponse fromSavedPlaylist(UserSavedPlaylist savedPlaylist){
        return PlaylistTitleResponse.builder()
                .id(savedPlaylist.getPlaylist().getId())
                .title(savedPlaylist.getPlaylist().getTitle())
                .build();
    }
}
