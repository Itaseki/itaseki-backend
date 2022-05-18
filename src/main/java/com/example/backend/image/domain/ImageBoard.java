package com.example.backend.image.domain;

import com.example.backend.report.Report;
import com.example.backend.user.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "imageBoard")
@Data
@NoArgsConstructor
public class ImageBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imageBoardId")
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private String imageBoardTitle;

    @Column(nullable = false)
    private Integer likeCount=0;

    @Column(nullable = false)
    private Integer reportCount=0;

    @Column(nullable = false)
    private Integer viewCount=0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "communityBoard",targetEntity = Report.class)
    private List<Report> reports=new ArrayList<>();

    @Builder
    public ImageBoard(String imageBoardTitle, String imageUrl, LocalDateTime createdTime, User user){
        this.imageBoardTitle = imageBoardTitle;
        this.imageUrl = imageUrl;
        this.createdTime = createdTime;
        this.user = user;
    }

    public void updateViewCount(){
        this.viewCount++;
    }
    public void updateLikeCount(int likeCount){this.likeCount+=likeCount;}
}
