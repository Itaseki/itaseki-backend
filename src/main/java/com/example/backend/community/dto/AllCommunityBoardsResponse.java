package com.example.backend.community.dto;

import com.example.backend.community.domain.CommunityBoard;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AllCommunityBoardsResponse {
    private Long id;
    private String title;
    private String writerNickname;
    private LocalDateTime createdTime;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;

    public static AllCommunityBoardsResponse fromEntity(CommunityBoard board){
        //UserNickname 파라미터, 빌더 추가
        return AllCommunityBoardsResponse.builder()
                .id(board.getId()).title(board.getTitle()).createdTime(board.getCreatedTime())
                .likeCount(board.getLikeCount()).viewCount(board.getViewCount()).commentCount(board.getComments().size())
                .writerNickname(board.getUser().getNickname())
                .build();
    }
}
