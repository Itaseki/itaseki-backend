package com.example.backend.playlist.repository;

import com.example.backend.playlist.domain.QPlaylistVideo;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.playlist.dto.TempPlaylistDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.backend.playlist.domain.QPlaylist.playlist;

@RequiredArgsConstructor
public class CustomPlaylistRepositoryImpl implements CustomPlaylistRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public TempPlaylistDto findAllPlaylistsWithPageable(Pageable pageable, List<String> queries, String nickname) {
        long pageOffset= pageable.getOffset()-4; //첫 페이지: 8개, 다음 페이지: 12개
        int pageSize = pageable.getPageSize();
        if(pageable.getPageNumber()==0){
            pageOffset=0;
            pageSize=8; //첫 페이지만 8개 조회
        }
        List<AllPlaylistsResponse> responses = jpaQueryFactory.select(Projections.fields(AllPlaylistsResponse.class,
                        playlist.id.as("id"), playlist.title.as("title"),
                        playlist.user.nickname.as("writerNickname"), playlist.likeCount.as("likeCount"), playlist.saveCount.as("saveCount")))
                .from(playlist)
                .where(predicate(queries, null), playlist.status.eq(true), playlist.isPublic.eq(true))
                .orderBy(order(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .fetch();

        long totalCount=responses.size();
        List<AllPlaylistsResponse> finalResponses = responses.stream().skip(pageOffset).limit(pageSize).collect(Collectors.toList());


        return new TempPlaylistDto(totalCount,finalResponses);
    }

    @Override
    public List<AllPlaylistsResponse> findBestPlaylists() {
        return jpaQueryFactory.select(Projections.fields(AllPlaylistsResponse.class,
                playlist.id.as("id"), playlist.title.as("title"),
                playlist.user.nickname.as("writerNickname"), playlist.likeCount.as("likeCount"), playlist.saveCount.as("saveCount")))
                .from(playlist)
                .where(playlist.status.eq(true),playlist.isPublic.eq(true))
                .orderBy(playlist.likeCount.desc(),playlist.id.desc())
                .limit(4)
                .fetch();
    }

    @Override
    public List<AllPlaylistsResponse> findAllForSearch(String sort, List<String> queries, String tag) {
        List<OrderSpecifier> orders=new ArrayList<>();
        if(sort.contains("like")){
            orders.add(new OrderSpecifier(Order.DESC, playlist.likeCount));
        }
        orders.add(new OrderSpecifier(Order.DESC, playlist.id));

        return jpaQueryFactory.select(Projections.fields(AllPlaylistsResponse.class,
                        playlist.id.as("id"), playlist.title.as("title"),
                        playlist.user.nickname.as("writerNickname"), playlist.likeCount.as("likeCount"), playlist.saveCount.as("saveCount")))
                .from(playlist)
                .where(predicate(queries, tag), playlist.status.eq(true), playlist.isPublic.eq(true))
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .limit(8)
                .fetch();
    }

    private BooleanExpression predicate(List<String> queries, String tag){
        return Expressions.allOf(checkQuery(queries), videoContainsTag(tag));
    }

    private BooleanExpression checkQuery(List<String> queryList){
        return Expressions.anyOf(queryList.stream().map(this::containsTitle).toArray(BooleanExpression[]::new));
    }

    private BooleanExpression containsTitle(String title){
        return playlist.title.contains(title).or(playlist.videos.any().video.description.contains(title));
    }

    private BooleanExpression videoContainsTag(String tag) {
        if (StringUtils.isNullOrEmpty(tag)) {
            return null;
        }
        QPlaylistVideo videoInPlaylist = playlist.videos.any();
        return videoInPlaylist.video.customHashtags.any().customHashtagName.eq(tag)
                .or(videoInPlaylist.video.videoHashtags.any().hashtag.hashtagName.eq(tag));
    }

    private List<OrderSpecifier> order(Sort sort){
        List<OrderSpecifier> orders=new ArrayList<>();
        Order direction= Order.DESC;
        for(Sort.Order order : sort){
            String orderProperty = order.getProperty();
            switch (orderProperty){
                case "id":
                    orders.add(new OrderSpecifier(direction,playlist.id));
                    return orders;
                case "likeCount":
                    orders.add(new OrderSpecifier(direction,playlist.likeCount));
                    break;
                case "saveCount":
                    orders.add(new OrderSpecifier(direction,playlist.saveCount));
                    break;
            }
        }
        return orders;
    }

}
