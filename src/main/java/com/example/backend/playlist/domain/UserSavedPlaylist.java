package com.example.backend.playlist.domain;

import com.example.backend.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import org.springframework.data.annotation.CreatedDate;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSavedPlaylist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_saved_playlist_id")
    private Long id;

    //1 사용자 - N 개의 플레이리스트 저장 가능
    //1 플레이리스트 - N 개의 사용자에 의해 저장될 수 있음 -> M:N 테이블 필요
    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "save_status")
    private Boolean status;

    @CreatedDate
    @Column
    private LocalDateTime createdTime;

}
