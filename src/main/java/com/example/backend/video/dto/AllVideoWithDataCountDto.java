package com.example.backend.video.dto;

import com.example.backend.video.domain.Video;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AllVideoWithDataCountDto {
    private final long FIRST_PAGE_SIZE = 8;
    private final long DEFAULT_PAGE_SIZE = 12;
    private long totalCount;
    private List<Video> videos;

    public int calculateTotalPageCount() {
         if (totalCount <= FIRST_PAGE_SIZE) {
            return 1;
        }
        return (int) (1 + Math.ceil((totalCount - FIRST_PAGE_SIZE) / (double) DEFAULT_PAGE_SIZE));
    }

    public AllVideoWithDataCountDto(List<Video> videos, long totalCount) {
        this.totalCount = totalCount;
        this.videos = videos;
    }
}
