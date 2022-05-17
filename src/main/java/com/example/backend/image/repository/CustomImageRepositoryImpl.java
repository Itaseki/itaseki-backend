package com.example.backend.image.repository;

import com.example.backend.image.domain.ImageBoard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomImageRepositoryImpl implements CustomImageRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ImageBoard> findBestBoards() {
        return null;
    }

    @Override
    public Page<ImageBoard> findAll(Pageable pageable, String[] queryList) {
        return null;
    }
}
