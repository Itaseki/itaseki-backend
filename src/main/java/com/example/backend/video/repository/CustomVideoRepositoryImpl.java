package com.example.backend.video.repository;

import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.customHashtag.QCustomHashtag;
import com.example.backend.video.domain.Hashtag;
import com.example.backend.video.domain.QVideoHashtag;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoHashtag;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
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

    @Override
    public List<Video> findBestVideos() {
        return jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true))
                .orderBy(video.likeCount.desc(), video.id.desc())
                .limit(4)
                .fetch();
    }

    @Override
    public Page<Video> findAll(Pageable pageable, List<String> tags, String nickname, List<String> queries) {
        QueryResults<Video> results = jpaQueryFactory.selectFrom(video)
                .where(video.status.eq(true), predicate(tags, nickname, queries))
                .orderBy(order(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(),pageable,results.getTotal());
    }

    private BooleanExpression predicate(List<String> tags, String nickname, List<String> queries){
        //해시태그에 있거나 키워드 (커스텀 해시태그) 에 있거나
        BooleanExpression tagExpression=null;
        BooleanExpression nicknameExpression=null;
        if(tags!=null){
            tagExpression = Expressions.anyOf(tags.stream()
                    .map(this::checkTag)
                    .toArray(BooleanExpression[]::new));
        }
        if(nickname!=null){
            nicknameExpression = checkNickname(nickname);
        }

        return Expressions.allOf(tagExpression,nicknameExpression);
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
