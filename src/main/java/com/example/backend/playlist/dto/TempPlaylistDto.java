package com.example.backend.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TempPlaylistDto {
    private long totalCount;
    private List<AllPlaylistsResponse> playlists;
}
