package com.example.backend.playlist.repository;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistVideo;
import com.example.backend.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, Long>, CustomPlaylistVideoRepository {
    Optional<PlaylistVideo> findByVideoAndPlaylist(Video video, Playlist playlist);
}
