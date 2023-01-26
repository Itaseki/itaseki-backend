package com.example.backend.myPage.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class MyPageCommentPageableResponse {
    private final int totalPageCount;
    private final List<DetailCommentResponse> data;

    private MyPageCommentPageableResponse(int count, List<DetailCommentResponse> data) {
        this.totalPageCount = count;
        this.data = data;
    }

    public static MyPageCommentPageableResponse of(List<DetailCommentResponse> data, int pageCount) {
        return new MyPageCommentPageableResponse(pageCount, data);
    }
}
