package com.example.backend.playlist.domain;

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
@Entity
@Table
@NoArgsConstructor
public class PlaylistComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    private Boolean isParentComment; //true 면 원댓글

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable=false, name = "comment_status")
    private Boolean status=true;

    @ManyToOne(targetEntity = PlaylistComment.class)
    @JoinColumn(name = "parent_comment_id")
    @JsonBackReference
    private PlaylistComment parentComment;

    @OneToMany(mappedBy = "parentComment", targetEntity = PlaylistComment.class)
    @JsonManagedReference
    private List<PlaylistComment> childComments=new ArrayList<>();

    @ManyToOne(targetEntity = Playlist.class)
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "playlistComment", targetEntity = Report.class)
    private List<Report> reports;

    @Builder
    public PlaylistComment(String content, Long parentId,Playlist playlist, User user){
        this.content=content;
        this.isParentComment= parentId==0;
        this.createdTime=LocalDateTime.now();
        this.playlist=playlist;
        this.user=user;
    }

}
