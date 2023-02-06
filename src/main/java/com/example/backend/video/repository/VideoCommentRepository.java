package com.example.backend.video.repository;

import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {
    List<VideoComment> findAllByUser(User user);
    Optional<VideoComment> findByIdAndStatusAndVideo(long id, boolean status, Video video);
}
