package com.example.backend.playlist.repository;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long>, CustomPlaylistRepository {
    List<Playlist> findAllByUser(User user);
}
