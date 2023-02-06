package com.example.backend.video.dto;

import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoCommentRequest {
    private String content;
    private Long parentCommentId;

    public VideoComment toEntity(User user, Video video, VideoComment parentComment) {
        return VideoComment.builder()
                .content(content)
                .user(user)
                .video(video)
                .parentComment(parentComment)
                .build();
    }
}
