package com.example.backend.video.repository;

import com.amazonaws.util.StringUtils;
import com.example.backend.video.domain.Video;
import com.example.backend.video.dto.TempVideoDto;
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
public class CustomVideoRepositoryImpl implements CustomVideoRepository{
    private final JPAQueryFactory jpaQueryFactory;
    private final String EMPTY = "";

    @Override
    public List<Video> findBestVideos(int videoCount) {
        return jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true))
                .orderBy(video.likeCount.desc(), video.id.desc())
                .limit(videoCount)
                .fetch();
    }

    @Override
    public List<Video> findTitleLike(String searchTitle,String order) {
        OrderSpecifier orderSpecifier = new OrderSpecifier(Order.DESC, video.id);
        if(order.equals("likeCount"))
            orderSpecifier=new OrderSpecifier(Order.DESC,video.likeCount);
        return jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true), video.description.containsIgnoreCase(searchTitle))
                .orderBy(orderSpecifier) //좋아요순 정렬
                .fetch();

    }

    @Override
    public Page<Video> findAllForSearch(List<String> tags, List<String> queries, Pageable pageable) { // pageable 추가 + tag 한 개로
        return new PageImpl<>(jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true), predicate(tags, queries))
                .orderBy(order(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(), pageable, calculateTotalPageCount(tags, queries));
    }

    private Long calculateTotalPageCount(List<String> tags, List<String> queries) {
        return jpaQueryFactory.select(video.count())
                .from(video)
                .where(video.status.eq(true), predicate(tags, queries))
                .fetchOne();
    }

    @Override
    public TempVideoDto findAllByPageable(Pageable pageable) {
        long pageOffset= pageable.getOffset() - 4; // 첫 페이지는 4개, 그 이후부터는 8개 조회
        int pageSize = pageable.getPageSize();
        if(pageable.getPageNumber() == 0){
            pageOffset=0;
            pageSize=8;
        }
        List<Video> videos = jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true))
                .orderBy(order(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageOffset)
                .limit(pageSize)
                .fetch();

        Long totalCount = jpaQueryFactory.select(video.count())
                .from(video)
                .where(video.status.eq(true))
                .fetchOne();

        if (totalCount == null) {
            totalCount = 0L;
        }

        return new TempVideoDto(totalCount,videos);
    }

    private BooleanExpression predicate(List<String> tags, List<String> queries){
        return Expressions.allOf(checkTag(tags), checkQuery(queries));
    }

    private BooleanExpression checkQuery(List<String> queryList){
        if (queryList.isEmpty()) {
            return null;
        }
        return Expressions.anyOf(queryList.stream()
                .map(video.description::contains)
                .toArray(BooleanExpression[]::new));
    }

    private BooleanExpression checkTag(List<String> tags){
        if (tags.isEmpty()) {
            return null;
        }
        return Expressions.anyOf(tags.stream()
                .map(this::isTagInVideo)
                .toArray(BooleanExpression[]::new));
    }

    private BooleanExpression isTagInVideo(String tag) {
        if (StringUtils.isNullOrEmpty(tag)) {
            return null;
        }
        return video.videoHashtags.any().hashtag.hashtagName.eq(tag)
                .or(video.customHashtags.any().customHashtagName.eq(tag));
    }

    private List<OrderSpecifier> order(Sort sort){
        List<OrderSpecifier> orders=new ArrayList<>();
        for(Sort.Order order : sort){
            String orderProperty = order.getProperty();
            switch (orderProperty){
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
