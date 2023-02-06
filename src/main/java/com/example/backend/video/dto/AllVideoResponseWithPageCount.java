package com.example.backend.video.dto;

import com.example.backend.video.domain.Video;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class AllVideoResponseWithPageCount {
    private Integer totalPageCount;
    private List<AllVideoResponse> videosResponses;

    @Builder
    private AllVideoResponseWithPageCount(List<AllVideoResponse> responses, int count) {
        this.totalPageCount = count;
        this.videosResponses = responses;
    }

    public static AllVideoResponseWithPageCount fromAllVideoQuery(AllVideoWithDataCountDto videoQuery) {
        return new AllVideoResponseWithPageCount(convertToResponse(videoQuery.getVideos()),
                videoQuery.calculateTotalPageCount());
    }

    private static List<AllVideoResponse> convertToResponse(List<Video> videos) {
        return videos.stream()
                .map(AllVideoResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
