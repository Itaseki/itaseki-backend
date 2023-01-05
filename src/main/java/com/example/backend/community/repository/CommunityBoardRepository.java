package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityBoardRepository extends JpaRepository<CommunityBoard,Long>,CustomCommunityRepository {
    Page<CommunityBoard> findAll(Pageable pageable);
    List<CommunityBoard> findAllByUserOrderByCreatedTimeDesc(User user);
}
