package com.example.backend.search.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class SearchVideoPageableResponse {
    private final int totalPageCount;
    private final List<SearchVideoResponse> data;

    private SearchVideoPageableResponse(int count, List<SearchVideoResponse> data) {
        this.totalPageCount = count;
        this.data = data;
    }

    public static SearchVideoPageableResponse of(List<SearchVideoResponse> data, int count) {
        return new SearchVideoPageableResponse(count, data);
    }
}
