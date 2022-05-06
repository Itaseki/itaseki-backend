package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.QCommunityBoard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.backend.community.domain.QCommunityBoard.communityBoard;

@Repository
@RequiredArgsConstructor
public class CustomCommunityRepositoryImpl implements CustomCommunityRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommunityBoard> findBestBoards() {
        return jpaQueryFactory.selectFrom(communityBoard)
                .orderBy(communityBoard.likeCount.desc())
                .limit(5)
                .fetch();
    }
}
