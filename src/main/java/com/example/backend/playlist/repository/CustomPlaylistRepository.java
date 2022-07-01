package com.example.backend.playlist.repository;

import com.example.backend.playlist.dto.AllPlaylistsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPlaylistRepository {
    Page<AllPlaylistsResponse> findAllPlaylistsWithPageable(Pageable pageable, String title, String videoTitle);
}
