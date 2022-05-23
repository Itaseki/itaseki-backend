package com.example.backend.image.dto;

import com.example.backend.community.dto.AllCommunityBoardsResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllImageResponseWithPageCount {
    Integer totalPageCount;
    List<AllImageBoardsResponse> imageBoardsResponses;

    public AllImageResponseWithPageCount(int count, List<AllImageBoardsResponse> images){
        this.totalPageCount = count;
        this.imageBoardsResponses = images;
    }
}
