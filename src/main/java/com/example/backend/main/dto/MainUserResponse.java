package com.example.backend.main.dto;

import com.example.backend.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MainUserResponse {
    private Long id;
    private String profileUrl;

    public static MainUserResponse fromEntity(User user){
        return MainUserResponse.builder()
                .id(user.getUserId())
                .profileUrl(user.getProfileUrl())
                .build();
    }
}
