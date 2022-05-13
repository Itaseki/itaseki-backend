package com.example.backend.video.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoCommentDto {
    private String content;
    private Long parentCommentId;
}
