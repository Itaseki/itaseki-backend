package com.example.backend.playlist.repository;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.playlist.dto.TempPlaylistDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPlaylistRepository {
    TempPlaylistDto findAllPlaylistsWithPageable(Pageable pageable);
    List<AllPlaylistsResponse> findBestPlaylists(int count);
    Page<Playlist> findAllForSearch(Pageable pageable, List<String> q, String tag);
}
