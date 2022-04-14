package com.example.backend.community.dto;

import com.example.backend.community.domain.CommunityBoard;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class DetailCommunityBoardResponse {
    private Long id;
    private String title;
    private String content;
    private List<String> imageUrls; //등록된 순서대로 넘어감
    private LocalDateTime createdTime;
    private Integer viewCount;
    private Integer likeCount;
//    private Long writerId;
//    private String writerNickname;
//    private Boolean isThisUserWriter;
//    private Boolean isThisBoardLikedByUser; //현재 사용자가 이 게시글에 좋아요를 눌렀는가
    private Integer commentCount;
    private List<CommunityCommentsResponse> comments;


    public static DetailCommunityBoardResponse fromEntity(CommunityBoard board,List<CommunityCommentsResponse> comments,List<String> images){
//        User 객체, 요청사용자 id 파라미터 추가 + builder 패턴에 추가
        return DetailCommunityBoardResponse.builder()
                .id(board.getId()).title(board.getTitle()).content(board.getContent()).commentCount(board.getComments().size())
                .createdTime(board.getCreatedTime()).viewCount(board.getViewCount()).likeCount(board.getLikeCount())
                .comments(comments).imageUrls(images)
                .build();
    }
}
