package com.example.backend.video.domain;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.report.Report;
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
@Entity
@Table(name = "videoComment")
public class VideoComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    private Boolean isParentComment; //true 면 원댓글

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable=false, name = "comment_status")
    private Boolean status=true;

    @ManyToOne(targetEntity = VideoComment.class)
    @JoinColumn(name = "parent_comment_id")
    @JsonBackReference
    private VideoComment parentComment;

    @OneToMany(mappedBy = "parentComment", targetEntity = VideoComment.class)
    @JsonManagedReference
    private List<VideoComment> childComments=new ArrayList<>();

    @ManyToOne(targetEntity = Video.class)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "videoComment",targetEntity = Report.class)
    private List<Report> reports;

    @Builder
    public VideoComment(String content, Long parentId,Video video, User user){
        this.content=content;
        this.isParentComment= parentId==0;
        this.createdTime=LocalDateTime.now();
        this.video=video;
        this.user=user;
    }

}
