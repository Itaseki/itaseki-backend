package com.example.backend.community;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "communityBoards")
@Data
@NoArgsConstructor
public class CommunityBoard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "communityBoardId")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private Integer viewCount=0;

    @Column(nullable = false)
    private Integer likeCount=0;

    @Column(nullable = false)
    private Integer reportCount=0;

    //User column 추가 (ManyToOne)

    @Builder
    public CommunityBoard(String title, String content, LocalDateTime createdTime){
        this.title=title;
        this.content=content;
        this.createdTime=createdTime;
    }
}
