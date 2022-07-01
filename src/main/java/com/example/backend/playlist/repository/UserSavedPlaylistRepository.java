package com.example.backend.playlist.repository;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSavedPlaylistRepository extends JpaRepository<UserSavedPlaylist, Long> {
    UserSavedPlaylist findByUserAndPlaylist(User user, Playlist playlist);
    List<UserSavedPlaylist> findAllByUser(User user);
}
