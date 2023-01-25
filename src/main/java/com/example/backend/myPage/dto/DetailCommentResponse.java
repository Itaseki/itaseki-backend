package com.example.backend.myPage.dto;

import com.example.backend.myPage.BoardType;
import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.video.domain.VideoComment;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class DetailCommentResponse {
    private final Long boardId;
    private final String boardType;
    private final String content;
    private final String boardTitle;
    private final LocalDateTime createdTime;

    private DetailCommentResponse(Long id, String boardType, String content, String boardTitle, LocalDateTime createdTime) {
        this.boardId = id;
        this.boardType = boardType;
        this.content = content;
        this.boardTitle = boardTitle;
        this.createdTime = createdTime;
    }

    public static DetailCommentResponse ofVideo(VideoComment comment) {
        return new DetailCommentResponse(comment.getVideo().getId(),
                BoardType.findBoardType(comment.getVideo()),
                comment.getContent(),
                comment.getVideo().getDescription(),
                comment.getCreatedTime());
    }

    public static DetailCommentResponse ofPlaylist(PlaylistComment comment) {
        return new DetailCommentResponse(comment.getPlaylist().getId(),
                BoardType.findBoardType(comment.getPlaylist()),
                comment.getContent(),
                comment.getPlaylist().getTitle(),
                comment.getCreatedTime());
    }
}
