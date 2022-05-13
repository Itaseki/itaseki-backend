package com.example.backend.video.repository;

import com.example.backend.video.domain.VideoComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {
}
