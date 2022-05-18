package com.example.backend.video.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AllVideoResponseWithPageCount {
    private Integer totalPageCount;
    private List<AllVideoResponse> videosResponses;

    public AllVideoResponseWithPageCount(List<AllVideoResponse> responses, Integer count){
        this.totalPageCount=count;
        this.videosResponses=responses;
    }
}
