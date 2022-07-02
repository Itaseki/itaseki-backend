package com.example.backend.playlist.dto;

import lombok.Getter;

@Getter
public class PlaylistCommentDto {
    private String content;
    private Long parentCommentId;
}
