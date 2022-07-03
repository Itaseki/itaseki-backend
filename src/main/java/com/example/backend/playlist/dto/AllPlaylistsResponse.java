package com.example.backend.playlist.dto;

import com.example.backend.playlist.domain.Playlist;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AllPlaylistsResponse {
    private Long id;
    private String title;
    private String titleImageUrl;
    private String writerNickname;
    private Integer likeCount;
    private Integer saveCount;
    private Integer videoCount;

    public void updateData(String thumbnailUrl, Integer videoCount){
        this.titleImageUrl=thumbnailUrl;
        this.videoCount=videoCount;
    }

    public AllPlaylistsResponse(Playlist playlist){
        this.title=playlist.getTitle();
        this.writerNickname=playlist.getUser().getNickname();
        this.likeCount=playlist.getLikeCount();
        this.saveCount=playlist.getSaveCount();
        this.id=playlist.getId();
    }

}
