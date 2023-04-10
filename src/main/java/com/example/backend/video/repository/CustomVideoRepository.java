package com.example.backend.video.repository;

import com.example.backend.video.domain.Video;
import com.example.backend.video.dto.AllVideoWithDataCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomVideoRepository {
    AllVideoWithDataCountDto findAllByPageable(Pageable pageable);
    List<Video> findBestVideos(int videoCount);
    List<Video> findTitleLike(String searchTitle, String order);
    Page<Video> findAllForSearch(String tag, List<String> queries, String series, Pageable pageable);
}
