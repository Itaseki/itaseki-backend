package com.example.backend.playlist.repository;

import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.playlist.dto.TempPlaylistDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.backend.playlist.domain.QPlaylist.playlist;
import static com.example.backend.playlist.domain.QPlaylistVideo.playlistVideo;

@RequiredArgsConstructor
public class CustomPlaylistRepositoryImpl implements CustomPlaylistRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public TempPlaylistDto findAllPlaylistsWithPageable(Pageable pageable, String title, String videoTitle) {
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
                .where(containsTitle(title), containsVideo(videoTitle), playlist.status.eq(true), playlist.isPublic.eq(true))
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

    private BooleanExpression containsTitle(String title){
        if(title==null||title.isEmpty())
            return null;
        return playlist.title.contains(title);
    }

    private BooleanExpression containsVideo(String title){
        if(title==null||title.isEmpty())
            return null;
        return playlist.videos.any().video.description.contains(title);
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
