package com.example.backend.video.dto;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.video.domain.Hashtag;
import com.example.backend.video.domain.Series;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InnerInfoResponse {
    private Long id;
    private String name;

    public InnerInfoResponse(Series series){
        this.id=series.getId();
        this.name=series.getSeriesName();
    }

    public InnerInfoResponse(Hashtag hashtag){
        this.id=hashtag.getId();
        this.name=hashtag.getHashtagName();
    }

    public InnerInfoResponse(Playlist playlist){
        this.id=playlist.getId();
        this.name=playlist.getTitle();
    }
}
