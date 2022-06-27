package com.example.backend.playlist.repository;

import com.example.backend.playlist.domain.Playlist;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.example.backend.playlist.domain.QPlaylistVideo.playlistVideo;

@RequiredArgsConstructor
public class CustomPlaylistVideoRepositoryImpl implements CustomPlaylistVideoRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Integer findLastVideoOrder(Playlist playlist) {
        return jpaQueryFactory.select(playlistVideo.videoOrder)
                .from(playlistVideo)
                .where(playlistVideo.playlist.eq(playlist),playlistVideo.status.eq(true))
                .orderBy(playlistVideo.videoOrder.desc())
                .fetchOne();
    }
}
