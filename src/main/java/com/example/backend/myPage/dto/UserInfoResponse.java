package com.example.backend.myPage.dto;

import com.example.backend.user.domain.User;
import lombok.Getter;

@Getter
public class UserInfoResponse {
    private final String nickname;
    private final String profileUrl;
    private final String email;

    private UserInfoResponse(User user) {
        this.nickname = user.getNickname();
        this.profileUrl = user.getProfileUrl();
        this.email = user.getEmail();
    }

    public static UserInfoResponse fromUser(User user) {
        return new UserInfoResponse(user);
    }
}
