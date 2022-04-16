package com.example.backend.community.dto;

import com.example.backend.community.domain.CommunityComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class CommunityCommentsResponse {
    private Long id;
    private String content;
//    private Long writerId;
//    private String writerNickname;
    private LocalDateTime createdTime;
//    private Boolean isThisUserWriter; //현재 사용자가 이 댓글을 작성한 사람인가?
//    private Boolean isThisBoardWriterCommentWriter; //이 댓글 작성자와 게시글 작성자가 동일 인물인가?
    private List<CommunityCommentsResponse> nestedComments; //이 댓글이 부모댓글 (원댓글)인 경우, 자식 댓글들이 순서대로 들어감

    public static CommunityCommentsResponse fromEntity(CommunityComment comment){
        //User 파라미터 추가, 대댓글들은 나중에 따로 세팅
        return CommunityCommentsResponse.builder()
                .id(comment.getId()).content(comment.getContent()).createdTime(comment.getCreatedTime())
                .build();
    }

}
