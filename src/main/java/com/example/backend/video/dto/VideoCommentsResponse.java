package com.example.backend.video.dto;

import com.example.backend.community.domain.CommunityComment;
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
public class VideoCommentsResponse {
    private Long id;
    private String content;
    private Long writerId;
    private String writerNickname;
    private LocalDateTime createdTime;
    private Boolean isThisUserWriter; //현재 사용자가 이 댓글을 작성한 사람인가?
    private Boolean isThisBoardWriterCommentWriter; //이 댓글 작성자와 게시글 작성자가 동일 인물인가?
    private List<VideoCommentsResponse> nestedComments; //이 댓글이 부모댓글 (원댓글)인 경우, 자식 댓글들이 순서대로 들어감

    public static VideoCommentsResponse fromEntity(VideoComment comment, Long boardWriterId, Long loginId){
        User user = comment.getUser();
        return VideoCommentsResponse.builder()
                .id(comment.getId()).content(comment.getContent()).createdTime(comment.getCreatedTime())
                .writerId(user.getUserId()).writerNickname(user.getNickname())
                .isThisUserWriter(user.getUserId().equals(loginId)).isThisBoardWriterCommentWriter(user.getUserId().equals(boardWriterId))
                .build();
    }

}
