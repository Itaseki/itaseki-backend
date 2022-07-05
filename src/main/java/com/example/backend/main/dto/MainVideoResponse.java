package com.example.backend.main.dto;

import com.example.backend.video.dto.AllVideoResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MainVideoResponse {
    private Long id;
    private String title;
    private String writerNickname;
    private Integer likeCount;
    private List<String> tags;
    private String thumbnailUrl;

    public static MainVideoResponse fromAllResponse(AllVideoResponse v){
        return MainVideoResponse.builder()
                .id(v.getId())
                .title(v.getTitle())
                .writerNickname(v.getWriterNickname())
                .likeCount(v.getLikeCount())
                .thumbnailUrl(v.getThumbnailUrl())
                .build();
    }

    public void updateTags(List<String> tags){
        this.tags = tags.stream().limit(2).collect(Collectors.toList());
    }
}
