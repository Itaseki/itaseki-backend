package com.example.backend.video.repository;

import com.querydsl.core.util.StringUtils;
import com.example.backend.video.domain.Video;
import com.example.backend.video.dto.AllVideoWithDataCountDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.List;

import static com.example.backend.video.domain.QVideo.video;

@RequiredArgsConstructor
public class CustomVideoRepositoryImpl implements CustomVideoRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Video> findBestVideos(int videoCount) {
        return jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true))
                .orderBy(video.likeCount.desc(), video.id.desc())
                .limit(videoCount)
                .fetch();
    }

    @Override
    public List<Video> findTitleLike(String searchTitle, String order) {
        OrderSpecifier orderSpecifier = new OrderSpecifier(Order.DESC, video.id);
        if (order.equals("likeCount")) {
            orderSpecifier = new OrderSpecifier(Order.DESC, video.likeCount);
        }
        return jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true), video.description.containsIgnoreCase(searchTitle))
                .orderBy(orderSpecifier) //좋아요순 정렬
                .fetch();
    }

    @Override
    public Page<Video> findAllForSearch(String tag, List<String> queries, String series, Pageable pageable) { // pageable 추가 + tag 한 개로
        return new PageImpl<>(jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true), predicate(tag, queries, series))
                .orderBy(order(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(), pageable, calculateTotalPageCount(tag, queries, series));
    }

    private Long calculateTotalPageCount(String tag, List<String> queries, String series) {
        return jpaQueryFactory.select(video.count())
                .from(video)
                .where(video.status.eq(true), predicate(tag, queries, series))
                .fetchOne();
    }

    @Override
    public AllVideoWithDataCountDto findAllByPageable(Pageable pageable) {
        long pageOffset = pageable.getOffset() - 4;
        int pageSize = pageable.getPageSize();

        if (pageable.getPageNumber() == 0) {
            pageOffset = 0;
            pageSize = 8;
        }

        return new AllVideoWithDataCountDto(jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true))
                .orderBy(order(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageOffset)
                .limit(pageSize)
                .fetch(), calculateTotalDataCount());
    }

    private Long calculateTotalDataCount() {
        return jpaQueryFactory.select(video.count())
                .from(video)
                .where(video.status.eq(true))
                .fetchOne();
    }

    private BooleanExpression predicate(String tag, List<String> queries, String series) {
        return Expressions.allOf(checkTag(tag), checkQuery(queries), checkSeries(series));
    }

    private BooleanExpression checkQuery(List<String> queryList) {
        if (queryList.isEmpty()) {
            return null;
        }
        return Expressions.anyOf(queryList.stream()
                .map(video.description::contains)
                .toArray(BooleanExpression[]::new));
    }

    private BooleanExpression checkTag(String tag) {
        if (StringUtils.isNullOrEmpty(tag)) {
            return null;
        }
        return video.videoHashtags.any().hashtag.hashtagName.eq(tag)
                .or(video.customHashtags.any().customHashtagName.eq(tag));
    }

    private BooleanExpression checkSeries(String series) {
        if (StringUtils.isNullOrEmpty(series)) {
            return null;
        }
        return video.series.seriesName.contains(series);
    }

    private List<OrderSpecifier> order(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        for (Sort.Order order : sort) {
            String orderProperty = order.getProperty();
            switch (orderProperty) {
                case "likeCount":
                    orders.add(new OrderSpecifier(Order.DESC, video.likeCount));
                case "id":
                    orders.add(new OrderSpecifier(Order.DESC, video.id));
                    return orders;
            }
        }
        return orders;
    }
}
