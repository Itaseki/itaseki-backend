package com.example.backend.myPage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MySubscribeDto {
    private final List<SubscribeUserDto> mySubscribe;
    private final List<SubscribeUserDto> recommendedSubscribe;

    @Builder
    public MySubscribeDto(List<SubscribeUserDto> mySubscribe, List<SubscribeUserDto> recommendedSubscribe) {
        this.mySubscribe = mySubscribe;
        this.recommendedSubscribe = recommendedSubscribe;
    }
}
