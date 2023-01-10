package com.example.backend.myPage.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class LikeDataDto {
    private final List<MyPageVideoDto> video;
    private final List<MyPageCommunityDto> community;
    private final List<MyPageImageDto> image;

    public LikeDataDto(List<MyPageVideoDto> video, List<MyPageCommunityDto> community, List<MyPageImageDto> image) {
        this.video = video;
        this.community = community;
        this.image = image;
    }
}
