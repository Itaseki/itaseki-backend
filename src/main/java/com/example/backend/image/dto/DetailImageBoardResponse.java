package com.example.backend.image.dto;

import com.example.backend.image.domain.ImageBoard;
import com.example.backend.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class DetailImageBoardResponse {

    private Long id;
    private String imageBoardTitle;
    private String imageUrl;
    private LocalDateTime createdTime;
    private Integer viewCount;
    private Integer likeCount;
    private Long writerId;
    private String writerNickname;
    private Boolean isThisUserWriter;
    private List<String> hashtags;

    public static DetailImageBoardResponse fromEntity(ImageBoard imageBoard, Long loginId, List<String> hashtags){
        User imageWriter = imageBoard.getUser();
        return DetailImageBoardResponse.builder()
                .id(imageBoard.getId()).imageBoardTitle(imageBoard.getImageBoardTitle())
                .createdTime(imageBoard.getCreatedTime()).viewCount(imageBoard.getViewCount()).likeCount(imageBoard.getLikeCount())
                .writerId(imageWriter.getUserId()).writerNickname(imageWriter.getNickname()).isThisUserWriter(imageWriter.getUserId().equals(loginId))
                .imageUrl(imageBoard.getImageUrl()).hashtags(hashtags)
                .build();
    }

}
