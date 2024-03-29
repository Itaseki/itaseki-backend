package com.example.backend.search.dto;

import com.example.backend.video.domain.Video;
import com.example.backend.video.dto.AllVideoResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SearchVideoResponse {
    private Long id;
    private String title;
    private String writerNickname;
    private Integer likeCount;
    private List<String> tags;
    private String thumbnailUrl;

    public static SearchVideoResponse fromEntity(Video video) {
        return SearchVideoResponse.builder()
                .id(video.getId())
                .title(video.getDescription())
                .writerNickname(video.getUser().getNickname())
                .likeCount(video.getLikeCount())
                .thumbnailUrl(video.getThumbnailUrl())
                .build();
    }

    public void updateTags(List<String> tags){
        this.tags = tags.stream().limit(2).collect(Collectors.toList());
    }
}
