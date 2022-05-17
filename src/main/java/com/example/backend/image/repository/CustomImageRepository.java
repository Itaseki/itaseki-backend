package com.example.backend.image.repository;

import com.example.backend.image.domain.ImageBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CustomImageRepository {
    List<ImageBoard> findBestBoards();
    Page<ImageBoard> findAll(Pageable pageable, String[] queryList);
}