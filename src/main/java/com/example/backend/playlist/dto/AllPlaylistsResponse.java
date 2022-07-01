package com.example.backend.playlist.dto;

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

}
