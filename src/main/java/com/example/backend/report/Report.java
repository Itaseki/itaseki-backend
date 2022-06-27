package com.example.backend.report;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Data
@NoArgsConstructor
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    //하나의 게시글에 여러개의 신고 -> M: Report 1 : CommunityBoard
    @ManyToOne
    @JoinColumn(name = "community_board_id")
    private CommunityBoard communityBoard;

    @ManyToOne
    @JoinColumn(name = "image_board_id")
    private ImageBoard imageBoard;

    //하나의 사용자가 여러개의 신고 가능
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_comment_id")
    private CommunityComment communityComment;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "video_comment_id")
    private VideoComment videoComment;

    //신고사유는 어떻게 하기로 했더라
    @Builder
    public Report (CommunityBoard communityBoard,User user,CommunityComment communityComment,Video video, VideoComment videoComment, ImageBoard imageBoard){
        this.imageBoard = imageBoard;
        this.communityBoard=communityBoard;
        this.user=user;
        this.communityComment=communityComment;
        this.video=video;
        this.videoComment=videoComment;
    }
}
