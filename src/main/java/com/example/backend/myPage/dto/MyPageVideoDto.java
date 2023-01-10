package com.example.backend.myPage.dto;

import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageVideoDto {
    private final Long id;
    private final String title;
    private final String writerNickname;
    private final int likeCount;
    private final String thumbnailUrl;

    public static MyPageVideoDto of(Video video) {
        return MyPageVideoDto.builder()
                .video(video)
                .build();
    }

    @Builder
    private MyPageVideoDto(Video video) {
        this.id = video.getId();
        this.title = video.getDescription();
        this.writerNickname = video.getUser().getNickname();
        this.likeCount = video.getLikeCount();
        this.thumbnailUrl = video.getThumbnailUrl();

    }
}
