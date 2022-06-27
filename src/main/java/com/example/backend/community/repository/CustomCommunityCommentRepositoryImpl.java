package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.domain.QCommunityComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.backend.community.domain.QCommunityComment.communityComment;

@RequiredArgsConstructor
public class CustomCommunityCommentRepositoryImpl implements CustomCommunityCommentRepository{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<CommunityComment> findAllCommentsOnBoard(CommunityBoard board) {
        return jpaQueryFactory.selectFrom(communityComment)
                .where(communityComment.communityBoard.eq(board), communityComment.status.eq(true))
                .fetch();
    }
}
