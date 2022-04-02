package com.example.backend.Community;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "communityPosts")
@Data
@NoArgsConstructor
public class CommunityPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityBoardId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(columnDefinition = "integer default 0")
    private Integer viewCount=0;

    @Column(columnDefinition = "integer default 0")
    private Integer likeCount=0;

    @Builder
    public CommunityPost(String title, String content, LocalDateTime createdTime){
        this.title=title;
        this.content=content;
        this.createdTime=createdTime;
    }
}
