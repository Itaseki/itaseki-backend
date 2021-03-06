package com.example.backend.playlist.repository;

import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.playlist.dto.TempPlaylistDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPlaylistRepository {
    TempPlaylistDto findAllPlaylistsWithPageable(Pageable pageable, List<String> queries, String nickname);
    List<AllPlaylistsResponse> findBestPlaylists();
    List<AllPlaylistsResponse> findAllForSearch(String sort, String nickname, List<String> q);
}
