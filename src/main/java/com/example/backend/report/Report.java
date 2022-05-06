package com.example.backend.report;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.user.domain.User;
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

    //하나의 사용자가 여러개의 신고 가능
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //신고사유는 어떻게 하기로 했더라
    @Builder
    public Report (CommunityBoard board,User user){
        this.communityBoard=board;
        this.user=user;
    }
}
