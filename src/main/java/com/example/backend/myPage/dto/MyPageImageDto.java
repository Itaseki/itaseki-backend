package com.example.backend.myPage.dto;

import com.example.backend.image.domain.ImageBoard;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageImageDto {
    private final Long id;
    private final String imageUrl;
    private final String title;

    public static MyPageImageDto of(ImageBoard imageBoard) {
        return MyPageImageDto.builder()
                .imageBoard(imageBoard)
                .build();
    }

    @Builder
    private MyPageImageDto(ImageBoard imageBoard) {
        this.id = imageBoard.getId();
        this.imageUrl = imageBoard.getImageUrl();
        this.title = imageBoard.getImageBoardTitle();
    }
}
