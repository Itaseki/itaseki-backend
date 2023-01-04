package com.example.backend.myPage.dto;

import com.example.backend.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    private String nickname;
    private String profileUrl;
    private String description;
    private int playlistCount;
    private int subscribeCount;
    private int reportCount;

    public static UserInfoDto fromUserAndDetail(User user, int playlistCount, int subscribeCount, int reportCount) {
        return UserInfoDto.builder()
                .user(user)
                .playlistCount(playlistCount)
                .subscribeCount(subscribeCount)
                .reportCount(reportCount)
                .build();
    }

    @Builder
    private UserInfoDto(User user, int playlistCount, int subscribeCount, int reportCount) {
        this.nickname = user.getNickname();
        this.profileUrl = user.getProfileUrl();
        this.description = user.getUserDescription();
        this.playlistCount = playlistCount;
        this.subscribeCount = subscribeCount;
        this.reportCount = reportCount;
    }
}
