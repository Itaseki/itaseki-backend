package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CustomCommunityCommentRepository {
    List<CommunityComment> findAllCommentsOnBoard(CommunityBoard board);
}
