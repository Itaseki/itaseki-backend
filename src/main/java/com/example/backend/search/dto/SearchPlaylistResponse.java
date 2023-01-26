package com.example.backend.search.dto;

import com.example.backend.playlist.domain.Playlist;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class SearchPlaylistResponse {
    private Long id;
    private String title;
    private String titleImageUrl;
    private String writerNickname;
    private List<String> tags;
    private Integer likeCount;
    private Integer saveCount;
    private Integer videoCount;

    private SearchPlaylistResponse(Playlist playlist, String thumbnail, int videoCount) {
        this.title = playlist.getTitle();
        this.writerNickname = playlist.getUser().getNickname();
        this.likeCount = playlist.getLikeCount();
        this.saveCount = playlist.getSaveCount();
        this.id = playlist.getId();
        this.titleImageUrl = thumbnail;
        this.videoCount = videoCount;
    }

    public static SearchPlaylistResponse fromPlaylistAndData(Playlist playlist, String thumbnail, int videoCount) {
        return new SearchPlaylistResponse(playlist, thumbnail, videoCount);
    }

    public void updateTags(List<String> tags){
        this.tags = tags.stream().limit(2).collect(Collectors.toList());
    }

}
