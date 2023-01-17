package com.example.backend.video.repository;

import com.example.backend.video.domain.Video;
import com.example.backend.video.dto.TempVideoDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.List;

import static com.example.backend.video.domain.QVideo.video;

@RequiredArgsConstructor
public class CustomVideoRepositoryImpl implements CustomVideoRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Video> findBestVideos() {
        return jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true))
                .orderBy(video.likeCount.desc(), video.id.desc())
                .limit(4)
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
    public List<Video> findAllForSearch(List<String> tags, List<String> queries, String sort) {
        List<OrderSpecifier> orders=new ArrayList<>();
        if(sort.contains("like")){
            orders.add(new OrderSpecifier(Order.DESC, video.likeCount));
        }
        orders.add(new OrderSpecifier(Order.DESC, video.id));

        return jpaQueryFactory.selectFrom(video)
                    .where(video.status.eq(true), predicate(tags, queries))
                    .orderBy(orders.toArray(OrderSpecifier[]::new))
                    .limit(8)
                    .fetch();

    }

    @Override
    public TempVideoDto findAll(Pageable pageable, List<String> tags, String nickname, List<String> queries) {
        long pageOffset= pageable.getOffset() - 4; // 첫 페이지는 4개, 그 이후부터는 8개 조회
        int pageSize = pageable.getPageSize();
        if(pageable.getPageNumber() == 0){
            pageOffset=0;
            pageSize=4;
        }
        List<Video> videos = jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true), predicate(tags, queries))
                .orderBy(order(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageOffset)
                .limit(pageSize)
                .fetch();

        Long totalCount = jpaQueryFactory.select(video.count())
                .from(video)
                .where(video.status.eq(true), predicate(tags, queries))
                .fetchOne();

        return new TempVideoDto(totalCount,videos);
    }

    private BooleanExpression predicate(List<String> tags, List<String> queries){
        return Expressions.anyOf(checkTag(tags), checkQuery(queries));
    }

    private BooleanExpression checkQuery(List<String> queryList){
        return Expressions.anyOf(queryList.stream()
                .map(video.description::contains)
                .toArray(BooleanExpression[]::new));
    }

    private BooleanExpression checkTag(List<String> tags){
        return Expressions.anyOf(tags.stream()
                .map(this::isTagInVideo)
                .toArray(BooleanExpression[]::new));
    }

    private BooleanExpression isTagInVideo(String tag) {
        return video.videoHashtags.any().hashtag.hashtagName.eq(tag)
                .or(video.customHashtags.any().customHashtagName.eq(tag));
    }

    private List<OrderSpecifier> order(Sort sort){
        List<OrderSpecifier> orders=new ArrayList<>();
        for(Sort.Order order : sort){
            String orderProperty = order.getProperty();
            switch (orderProperty){
                case "id":
                    orders.add(new OrderSpecifier(Order.DESC, video.id));
                    return orders;
                case "likeCount":
                    orders.add(new OrderSpecifier(Order.DESC, video.likeCount));
                    break;
            }
        }
        return orders;
    }
}
