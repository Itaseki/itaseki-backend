package com.example.backend.myPage.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class MyPagePageableResponse {
    private final int totalPageCount;
    private final List<DetailDataResponse> data;

    private MyPagePageableResponse(int count, List<DetailDataResponse> data) {
        this.totalPageCount = count;
        this.data = data;
    }

    public static MyPagePageableResponse of(List<DetailDataResponse> data, int pageCount) {
        return new MyPagePageableResponse(pageCount, data);
    }
}
