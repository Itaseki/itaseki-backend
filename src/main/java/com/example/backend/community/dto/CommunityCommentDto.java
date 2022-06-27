package com.example.backend.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityCommentDto {
    private String content;
    private Long parentCommentId; //대댓글이라면? 이 대댓글이 어떤 댓글에 달린 건지를 명시 -> 원댓글(부모댓글)의 id를 같이 보내주어야함
    //대댓글이 아니라면 (즉 이 댓글이 새롭게 달린 원댓글이라면) parentCommentId는 0
}
