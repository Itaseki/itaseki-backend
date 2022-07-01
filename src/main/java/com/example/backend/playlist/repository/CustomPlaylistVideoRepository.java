package com.example.backend.playlist.repository;

import com.example.backend.playlist.domain.Playlist;

public interface CustomPlaylistVideoRepository {
    Integer findLastVideoOrder(Playlist playlist);
    String findFirstThumbnailUrl(Playlist playlist);
}
