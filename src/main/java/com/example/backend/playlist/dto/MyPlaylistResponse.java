package com.example.backend.playlist.dto;

import com.example.backend.playlist.domain.Playlist;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPlaylistResponse {
    private Long id;
    private String title;
    private Boolean isPublic;

    public static MyPlaylistResponse fromEntity(Playlist playlist){
        return MyPlaylistResponse.builder()
                .id(playlist.getId())
                .title(playlist.getTitle())
                .isPublic(playlist.getIsPublic())
                .build();
    }
}
