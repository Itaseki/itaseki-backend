package com.example.backend.image.repository;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.image.domain.QImageBoard;
import com.querydsl.core.QueryResults;
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
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.example.backend.image.domain.QImageBoard.imageBoard;

@Repository
@RequiredArgsConstructor
public class CustomImageRepositoryImpl implements CustomImageRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ImageBoard> findBestBoards() {
        return jpaQueryFactory.selectFrom(imageBoard)
                .where(imageBoard.status.eq(true))
                .orderBy(imageBoard.likeCount.desc(),imageBoard.id.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public Page<ImageBoard> findAll(Pageable pageable, String[] queryList) {
        QueryResults<ImageBoard> queryResults = jpaQueryFactory.selectFrom(imageBoard)
                .where(imageBoard.status.eq(true), getPredicate(queryList))
                .orderBy(getOrders(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset()) //시작 위치
                .limit(pageable.getPageSize()) //한 페이지에 들어가는 데이터 개수만큼만 조회
                .fetchResults();
        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    private BooleanExpression getPredicate(String[] queryList){
        if(queryList==null)
            return null;
        return Expressions.anyOf(Arrays.stream(queryList).map(this::checkTitleAndContent).toArray(BooleanExpression[]::new));

    }

    private BooleanExpression checkTitleAndContent(String q){
        return imageBoard.imageBoardTitle.contains(q).or(imageBoard.imageUrl.contains(q));
    }

    private List<OrderSpecifier> getOrders(Sort sort) {
        List<OrderSpecifier> orders=new ArrayList<>();
        for (Sort.Order order : sort) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            switch (order.getProperty()){
                case "id":
                    orders.add(new OrderSpecifier(direction,imageBoard.id));
                    return orders;
                case "likeCount":
                    orders.add(new OrderSpecifier(direction,imageBoard.likeCount));
                    break;
                case "viewCount":
                    orders.add(new OrderSpecifier(direction,imageBoard.viewCount));
                    break;
            }
        }
        return orders;
    }
}
