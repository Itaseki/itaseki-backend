package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityBoardRepository extends JpaRepository<CommunityBoard,Long> {
    Page<CommunityBoard> findAll(Pageable pageable);
}
