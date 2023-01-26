package com.example.backend.search.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class SearchPageableResponse {
    private final int totalPageCount;
    private final List<SearchVideoResponse> data;

    private SearchPageableResponse(int count, List<SearchVideoResponse> data) {
        this.totalPageCount = count;
        this.data = data;
    }

    public static SearchPageableResponse of(List<SearchVideoResponse> data, int count) {
        return new SearchPageableResponse(count, data);
    }
}
