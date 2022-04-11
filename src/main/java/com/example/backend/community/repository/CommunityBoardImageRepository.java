package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityBoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityBoardImageRepository extends JpaRepository<CommunityBoardImage,Long> {
}
