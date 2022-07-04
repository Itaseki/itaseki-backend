package com.example.backend.like;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
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

    @ManyToOne
    @JoinColumn(name = "imageBoardId")
    private ImageBoard imageBoard;

    @ManyToOne
    @JoinColumn(name = "videoId")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "playlistId")
    private Playlist playlist;

    @Builder
    public Like(User user, CommunityBoard communityBoard, Video video, ImageBoard imageBoard, Playlist playlist){
        this.user=user;
        this.communityBoard=communityBoard;
        this.video=video;
        this.imageBoard = imageBoard;
        this.playlist=playlist;
    }

    public Boolean modifyLikeStatus(){
        this.likeStatus=!this.likeStatus;
        return this.likeStatus;
    }
}
