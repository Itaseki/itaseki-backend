package com.example.backend.playlist.repository;

import com.example.backend.playlist.domain.PlaylistComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistCommentRepository extends JpaRepository<PlaylistComment, Long> {
}
