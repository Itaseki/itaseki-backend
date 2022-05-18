package com.example.backend.like;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.user.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "likes")
@Data
@NoArgsConstructor
public class Like {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likeId")
    private Long id;

    @Column(nullable = false)
    private Boolean likeStatus = true;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "communityBoardId")
    private CommunityBoard communityBoard;

    @Builder
    public Like(User user, CommunityBoard communityBoard){
        this.user=user;
        this.communityBoard=communityBoard;
    }

    public Boolean modifyLikeStatus(){
        this.likeStatus=!this.likeStatus;
        return this.likeStatus;
    }
}
