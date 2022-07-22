package com.example.backend.image.dto;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.dto.AllCommunityBoardsResponse;
import com.example.backend.image.domain.ImageBoard;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AllImageBoardsResponse {
    private Long id;
    private String imageBoardTitle;
    private String writerNickname;
    private LocalDateTime createdTime;
    private Integer viewCount;
    private Integer likeCount;
    private String imageUrl;

    public static AllImageBoardsResponse fromEntity(ImageBoard imageBoard){
        return AllImageBoardsResponse.builder()
                .id(imageBoard.getId()).imageBoardTitle(imageBoard.getImageBoardTitle()).createdTime(imageBoard.getCreatedTime())
                .likeCount(imageBoard.getLikeCount()).viewCount(imageBoard.getViewCount())
                .writerNickname(imageBoard.getUser().getNickname()).imageUrl(imageBoard.getImageUrl())
                .build();
    }
}
