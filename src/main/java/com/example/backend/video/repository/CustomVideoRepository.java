package com.example.backend.video.repository;

import com.example.backend.video.domain.Video;
import com.example.backend.video.dto.TempVideoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomVideoRepository {
    TempVideoDto findAll(Pageable pageable, List<String> tags, String nickname, List<String> queries);
    List<Video> findBestVideos();
    List<Video> findTitleLike(String searchTitle, String order);
    List<Video> findAllForSearch(List<String> tags, String nickname, List<String> queries, String sort);
}
