package com.example.backend.community.repository;

import com.example.backend.community.domain.CommunityBoard;

import java.util.List;

public interface CustomCommunityRepository {
    List<CommunityBoard> findBestBoards();
}
