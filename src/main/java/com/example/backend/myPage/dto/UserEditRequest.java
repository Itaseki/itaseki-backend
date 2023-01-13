package com.example.backend.myPage.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserEditRequest {
    private String nickname;
    private String description;
}
