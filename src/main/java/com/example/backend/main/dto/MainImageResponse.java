package com.example.backend.main.dto;

import com.example.backend.image.domain.ImageBoard;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MainImageResponse {
    private Long id;
    private String imageUrl;
    private Integer likeCount;

    public static MainImageResponse fromEntity(ImageBoard board){
        return MainImageResponse.builder()
                .id(board.getId())
                .imageUrl(board.getImageUrl())
                .likeCount(board.getLikeCount())
                .build();
    }
}
