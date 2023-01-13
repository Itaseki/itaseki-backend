package com.example.backend.user.repository;

import static com.example.backend.user.domain.QSubscribe.subscribe;

import com.example.backend.myPage.dto.SubscribeUserDto;
import com.example.backend.user.domain.User;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomSubscribeRepositoryImpl implements CustomSubscribeRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SubscribeUserDto> findAllNonSubscribingTargets(User user, long recommendationLimit) {
        return jpaQueryFactory.select(Projections.fields(SubscribeUserDto.class, subscribe.subscribeTarget.nickname.as("nickname"),
                subscribe.subscribeTarget.profileUrl.as("profileUrl"),
                subscribe.count().as("subscribeCount")))
                .from(subscribe)
                .where(predicateNonSubscribe(user.getUserId()))
                .groupBy(subscribe.subscribeTarget.nickname, subscribe.subscribeTarget.profileUrl)
                .having(subscribe.count().goe(recommendationLimit))
                .fetch();
    }

    private BooleanExpression predicateNonSubscribe(Long userId) {
        return subscribe.status.isTrue().and(subscribe.user.userId.ne(userId)).and(subscribe.subscribeTarget.userId.ne(userId));
    }
}
