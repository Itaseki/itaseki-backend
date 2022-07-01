package com.example.backend.playlist.domain;

import com.example.backend.user.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@Data
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    //1 user - N playlist / 1 playlist - 1 user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //1 playlist - N video / 1 video - N playlist => M:N
    @Column(nullable = false)
    private LocalDateTime createdTime;

    //디폴트값이 있던가?
    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private Integer likeCount=0;

    @Column(nullable = false)
    private int viewCount=0;

    @Column(nullable = false)
    private int saveCount=0;

    @Column(nullable = false, name = "playlist_status")
    private Boolean status=true;

    @OneToMany(mappedBy = "playlist")
    private List<PlaylistVideo> videos;

    //영상 자체는 playlistVideo 에 따로 저장
    @Builder
    public Playlist(LocalDateTime now, User user, String title, Boolean isPublic){
        this.createdTime=now;
        this.isPublic=isPublic;
        this.user=user;
        this.title=title;
    }

    public void modifyPlaylistPublicStatus(){
        this.isPublic=!this.isPublic;
    }

    public void updateSaveCount(){this.saveCount++;}


}
