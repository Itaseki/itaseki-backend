package com.example.backend.playlist.domain;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table
@NoArgsConstructor
public class PlaylistVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_video_id")
    private Long id;

    //M:N 테이블의 중간자 -> 1 video  : N PlaylistVideo  /  1 playlist : N playlistVideo
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @Column(nullable = false, name = "playlist_video_status")
    private Boolean status=true;

    @Column(nullable = false)
    private int videoOrder;

    @Builder
    public PlaylistVideo (Video video, Playlist playlist, int order){
        this.video=video;
        this.playlist=playlist;
        this.videoOrder=order;
    }

    public void modifyVideoOrder(int order){
        this.videoOrder=order;
    }

}
