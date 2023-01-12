package com.example.backend.myPage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyDataDto {
    private final List<MyPageCommunityDto> communityDto;
    private final List<MyPageVideoDto> videoDto;
    private final List<MyPageImageDto> imageDto;
    private final List<MyCommentDto> commentDto;

    @Builder
    public MyDataDto(List<MyPageCommunityDto> communityDto, List<MyPageVideoDto> videoDto, List<MyPageImageDto> imageDto, List<MyCommentDto> commentDto) {
        this.communityDto = communityDto;
        this.videoDto = videoDto;
        this.imageDto = imageDto;
        this.commentDto = commentDto;
    }
}
