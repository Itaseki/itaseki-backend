package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityBoardRepository extends JpaRepository<CommunityBoard,Long> {
}
