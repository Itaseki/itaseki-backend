package com.example.backend.community.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "communityComments")
public class CommunityComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //mysql은 pk값 자동증가로 IDENTITY 를 사용한다
    @Column(name = "communityCommentId")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isParentComment; //true 면 원댓글 (부모댓글) 이라는 의미

    @Column
    private Long commentGroupId;
    //원댓글과 그 대댓글들의 묶음이 몇 번 그룹인지에 대한 내용

    @Column(nullable = false)
    private LocalDateTime createdTime;

    //하나의 게시글에 여러 댓글 -> 게시글:댓글 = 1:M
    //댓글이 M 이므로 댓글 엔티티에서 ManyToOne 사용, comment 엔티티가 연관관계의 주인
    @ManyToOne(targetEntity = CommunityBoard.class)
    @JoinColumn(name = "communityBoardId")
    private CommunityBoard communityBoard;

    //manyToOne User 연관관계 매핑 추가 필요

    @Builder
    public CommunityComment(String content, Long parentId,LocalDateTime createdTime, CommunityBoard communityBoard){
        this.content=content;
        this.isParentComment= parentId==0;
        this.createdTime=createdTime;
        this.communityBoard=communityBoard;
    }

    public void setCommentGroupId(Long groupId){
        this.commentGroupId=groupId;
    }
}
