package com.example.backend.community.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllBoardResponseWithPageCount {
    Integer totalPageCount;
    List<AllCommunityBoardsResponse> boardsResponses;

    public AllBoardResponseWithPageCount(int count, List<AllCommunityBoardsResponse> boards){
        this.totalPageCount=count;
        this.boardsResponses=boards;
    }
}
