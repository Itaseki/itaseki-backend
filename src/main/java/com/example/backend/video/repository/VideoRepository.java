package com.example.backend.video.repository;

import com.example.backend.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long>, CustomVideoRepository {
    Video findByVideoUrlContains(@Param("url")String url);
}
