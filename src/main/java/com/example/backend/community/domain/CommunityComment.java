package com.example.backend.community.domain;

import com.example.backend.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity @Table(name = "communityComment")
public class CommunityComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //mysql은 pk값 자동증가로 IDENTITY 를 사용한다
    @Column(name = "communityCommentId")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    private Boolean isParentComment; //true 면 원댓글 (부모댓글) 이라는 의미

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @ManyToOne(targetEntity = CommunityComment.class)
    @JoinColumn(name = "parentCommentId")
    @JsonBackReference
    private CommunityComment parentComment;

    @OneToMany(mappedBy = "parentComment", targetEntity = CommunityComment.class)
    @JsonManagedReference
    private List<CommunityComment> childComments=new ArrayList<>();

    //하나의 게시글에 여러 댓글 -> 게시글:댓글 = 1:M
    //댓글이 M 이므로 댓글 엔티티에서 ManyToOne 사용, comment 엔티티가 연관관계의 주인
    @ManyToOne(targetEntity = CommunityBoard.class)
    @JoinColumn(name = "communityBoardId")
    private CommunityBoard communityBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public CommunityComment(String content, Long parentId, LocalDateTime createdTime, CommunityBoard communityBoard){
        this.content=content;
        this.isParentComment= parentId==0;
        this.createdTime=createdTime;
        this.communityBoard=communityBoard;
    }

}
