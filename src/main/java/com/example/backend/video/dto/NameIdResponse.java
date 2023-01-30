package com.example.backend.video.dto;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.video.domain.Hashtag;
import com.example.backend.video.domain.Series;
import lombok.Getter;

@Getter
public class NameIdResponse {
    private long id;
    private String name;

    public NameIdResponse(Series series) {
        this.id = series.getId();
        this.name = series.getSeriesName();
    }

    public NameIdResponse(Hashtag hashtag) {
        this.id = hashtag.getId();
        this.name = hashtag.getHashtagName();
    }

    public NameIdResponse(Playlist playlist) {
        this.id = playlist.getId();
        this.name = playlist.getTitle();
    }
}
