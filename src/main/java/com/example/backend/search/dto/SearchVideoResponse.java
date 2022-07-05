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
    private String thumbnailUrl;
    private List<String> tags;

    public static SearchVideoResponse fromAllResponse(AllVideoResponse response){
        return SearchVideoResponse.builder()
                .id(response.getId())
                .thumbnailUrl(response.getThumbnailUrl())
                .build();
    }

    public void updateTags(List<String> tags){
        this.tags = tags.stream().limit(2).collect(Collectors.toList());
    }
}
