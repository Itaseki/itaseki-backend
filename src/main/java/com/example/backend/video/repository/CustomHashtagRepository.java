package com.example.backend.video.repository;

import com.example.backend.video.domain.CustomHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomHashtagRepository extends JpaRepository<CustomHashtag, Long> {
}
