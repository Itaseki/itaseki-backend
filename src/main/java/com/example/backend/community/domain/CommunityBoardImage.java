package com.example.backend.community.domain;

import com.example.backend.community.domain.CommunityBoard;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "communityBoardImages")
public class CommunityBoardImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "communityBoardImageId")
    private Long id;

    //우선 단방향 매핑으로 시작
    @ManyToOne(targetEntity = CommunityBoard.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "communityBoardId")
    private CommunityBoard communityBoard;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String originFileName;

    @Column(nullable = false)
    private int imageOrder; //order는 mysql 내에서 예약어로 쓰이는 경우가 많음

    @Builder
    public CommunityBoardImage(CommunityBoard board,String url, String fileName,int order){
        this.communityBoard=board;
        this.imageUrl=url;
        this.originFileName=fileName;
        this.imageOrder=order;
    }

}
