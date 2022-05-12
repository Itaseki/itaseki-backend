package com.example.backend.video.repository;

import com.example.backend.video.domain.VideoHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoHashtagRepository extends JpaRepository<VideoHashtag, Long> {
}
