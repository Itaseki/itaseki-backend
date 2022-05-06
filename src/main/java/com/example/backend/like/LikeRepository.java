package com.example.backend.like;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    Like findByUserAndCommunityBoard(User user, CommunityBoard communityBoard);
    List<Like> findAllByCommunityBoard(CommunityBoard communityBoard);
}
