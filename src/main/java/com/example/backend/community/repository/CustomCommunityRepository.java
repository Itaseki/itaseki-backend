package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomCommunityRepository {
    List<CommunityBoard> findBestBoards();
    Page<CommunityBoard> findAll(Pageable pageable, String[] queryList);
}
