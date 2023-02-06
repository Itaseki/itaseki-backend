package com.example.backend.video.dto;

import com.example.backend.user.domain.User;
import com.example.backend.video.domain.VideoComment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class VideoCommentResponse {
    private Long id;
    private String content;
    private Long writerId;
    private String writerNickname;
    private LocalDateTime createdTime;
    private Boolean isThisUserWriter;
    private Boolean isThisBoardWriterCommentWriter;
    private List<VideoCommentResponse> nestedComments;

    public static VideoCommentResponse fromEntity(VideoComment comment, long boardWriterId, long loginId){
        User user = comment.getUser();
        return VideoCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdTime(comment.getCreatedTime())
                .writerId(user.getUserId())
                .writerNickname(user.getNickname())
                .isThisUserWriter(user.getUserId().equals(loginId))
                .isThisBoardWriterCommentWriter(user.getUserId().equals(boardWriterId))
                .build();
    }

}
