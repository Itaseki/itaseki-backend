package com.example.backend.video.repository;

import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long>, CustomVideoRepository {
    List<Video> findAllByVideoUrlContains(@Param("url")String url);
    List<Video> findAllByUserOrderByCreatedTimeDesc(User user);
}
