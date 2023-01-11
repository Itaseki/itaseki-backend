package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityComment;
import com.example.backend.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long>,CustomCommunityCommentRepository {
    List<CommunityComment> findAllByUser(User user);
}
