package com.example.backend.playlist.repository;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, Long>, CustomPlaylistVideoRepository {
}
