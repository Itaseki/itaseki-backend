package com.example.backend.search.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class SearchPlaylistPageableResponse {
    private final int totalPageCount;
    private final List<SearchPlaylistResponse> data;

    private SearchPlaylistPageableResponse(int count, List<SearchPlaylistResponse> data) {
        this.totalPageCount = count;
        this.data = data;
    }

    public static SearchPlaylistPageableResponse of(List<SearchPlaylistResponse> data, int count) {
        return new SearchPlaylistPageableResponse(count, data);
    }
}
