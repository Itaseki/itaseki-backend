package com.example.backend.main.dto;

import com.example.backend.community.dto.AllCommunityBoardsResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MainCommunityResponse {
    private Long id;
    private String title;
    private Integer likeCount;
    private Integer commentCount;

    public static MainCommunityResponse fromAllResponse(AllCommunityBoardsResponse board){
        return MainCommunityResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .likeCount(board.getLikeCount())
                .commentCount(board.getCommentCount())
                .build();
    }
}
