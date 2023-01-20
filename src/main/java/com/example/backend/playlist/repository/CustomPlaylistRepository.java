package com.example.backend.playlist.repository;

import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.playlist.dto.TempPlaylistDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPlaylistRepository {
    TempPlaylistDto findAllPlaylistsWithPageable(Pageable pageable);
    List<AllPlaylistsResponse> findBestPlaylists(int count);
    List<AllPlaylistsResponse> findAllForSearch(String sort, List<String> q, String tag);
}
