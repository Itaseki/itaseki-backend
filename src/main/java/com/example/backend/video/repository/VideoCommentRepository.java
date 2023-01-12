package com.example.backend.video.repository;

import com.example.backend.user.domain.User;
import com.example.backend.video.domain.VideoComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {
    List<VideoComment> findAllByUser(User user);
}
