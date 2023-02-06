package com.example.backend.video.dto;

import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AllVideoResponse {
    private Long id;
    private String title;
    private String writerNickname;
    private Integer likeCount;
    private String thumbnailUrl;

    public static AllVideoResponse fromEntity(Video video){
        return AllVideoResponse.builder()
                .id(video.getId())
                .title(video.getDescription())
                .writerNickname(video.getUser().getNickname())
                .likeCount(video.getLikeCount())
                .thumbnailUrl(video.getThumbnailUrl())
                .build();

    }
}
