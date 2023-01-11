package com.example.backend.myPage.dto;

import com.example.backend.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeUserDto {
    private String nickname;
    private String profileUrl;
    private long subscribeCount;

    public static SubscribeUserDto ofUser(User user, long count) {
        return new SubscribeUserDto(user.getNickname(), user.getProfileUrl(), count);
    }
}
