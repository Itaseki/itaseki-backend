package com.example.backend.myPage.dto;

import com.example.backend.user.domain.User;
import lombok.Getter;

@Getter
public class UserEditInfoDto {
    private final String nickname;
    private final String profileUrl;
    private final String description;

    private UserEditInfoDto(User user) {
        this.nickname = user.getNickname();
        this.profileUrl = user.getProfileUrl();
        this.description = user.getUserDescription();
    }

    public static UserEditInfoDto ofUser(User user) {
        return new UserEditInfoDto(user);
    }
}
