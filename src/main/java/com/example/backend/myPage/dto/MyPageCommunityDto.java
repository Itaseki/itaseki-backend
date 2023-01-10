package com.example.backend.myPage.dto;

import com.example.backend.community.domain.CommunityBoard;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageCommunityDto {
    private final Long id;
    private final String title;
    private final int likeCount;
    private final int viewCount;
    private final LocalDateTime createdTime;

    public static MyPageCommunityDto of(CommunityBoard communityBoard) {
        return MyPageCommunityDto.builder()
                .communityBoard(communityBoard)
                .build();
    }

    @Builder
    private MyPageCommunityDto(CommunityBoard communityBoard) {
        this.id = communityBoard.getId();
        this.title = communityBoard.getTitle();
        this.likeCount = communityBoard.getLikeCount();
        this.viewCount = communityBoard.getViewCount();
        this.createdTime = communityBoard.getCreatedTime();
    }
}
