package com.example.backend.video.repository;

import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.customHashtag.QCustomHashtag;
import com.example.backend.video.domain.QVideoHashtag;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoHashtag;
import com.example.backend.video.dto.TempVideoDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
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
    public List<Video> findAllForSearch(List<String> tags, String nickname, List<String> queries, String sort) {
        List<OrderSpecifier> orders=new ArrayList<>();
        if(sort.contains("like")){
            orders.add(new OrderSpecifier(Order.DESC, video.likeCount));
        }
        orders.add(new OrderSpecifier(Order.DESC, video.id));

        return jpaQueryFactory.selectFrom(video)
                    .where(video.status.eq(true), predicate(tags, nickname, queries))
                    .orderBy(orders.toArray(OrderSpecifier[]::new))
                    .limit(8)
                    .fetch();

    }

    @Override
    public TempVideoDto findAll(Pageable pageable, List<String> tags, String nickname, List<String> queries) {
        long pageOffset= pageable.getOffset()-4;
        int pageSize = pageable.getPageSize();
        if(pageable.getPageNumber()==0){
            pageOffset=0;
            pageSize=4;
        }
        List<Video> videos = jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true), predicate(tags, nickname, queries))
                .orderBy(order(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageOffset)
                .limit(pageSize)
                .fetch();

        Long totalCount = jpaQueryFactory.select(video.count())
                .from(video)
                .where(video.status.eq(true), predicate(tags, nickname, queries))
                .fetchOne();

        return new TempVideoDto(totalCount,videos);
    }

    private BooleanExpression predicate(List<String> tags, String nickname, List<String> queries){
        //해시태그에 있거나 키워드 (커스텀 해시태그) 에 있거나
        BooleanExpression tagExpression=null;
        BooleanExpression nicknameExpression=null;
        BooleanExpression queryExpressions=null;
        if(tags!=null){
            tagExpression = Expressions.anyOf(tags.stream()
                    .map(this::checkTag)
                    .toArray(BooleanExpression[]::new));
        }
        if(nickname!=null){
            nicknameExpression = checkNickname(nickname);
        }

        if(queries!=null){
            queryExpressions=checkQuery(queries);
        }

        return Expressions.allOf(tagExpression,nicknameExpression,queryExpressions);
    }

    private BooleanExpression checkQuery(List<String> queryList){
        if(queryList==null)
            return null;
        return Expressions.anyOf(queryList.stream().map(video.description::contains).toArray(BooleanExpression[]::new));

    }

    //join 쿼리로 바꿀 수 있나?
    private BooleanExpression checkTag(String tag){
        ListPath<VideoHashtag, QVideoHashtag> videoHashtags = video.videoHashtags; //이 비디오의 VideoHashtags 매핑 데이터들
        ListPath<CustomHashtag, QCustomHashtag> customHashtags = video.customHashtags;
        return videoHashtags.any().hashtag.hashtagName.eq(tag).or(customHashtags.any().customHashtagName.eq(tag)); //하나라도 현재 검색하는 hashtag 와 겹치는게 있는가?
    }

    private BooleanExpression checkNickname(String nickname){
        return video.user.nickname.eq(nickname);
    }


    private List<OrderSpecifier> order(Sort sort){
        List<OrderSpecifier> orders=new ArrayList<>();
        Order direction= Order.DESC;
        for(Sort.Order order : sort){
            String orderProperty = order.getProperty();
            switch (orderProperty){
                case "id":
                    orders.add(new OrderSpecifier(direction,video.id));
                    return orders;
                case "likeCount":
                    orders.add(new OrderSpecifier(direction,video.likeCount));
                    break;
            }
        }
        return orders;
    }
}
