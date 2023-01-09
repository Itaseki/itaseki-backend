package com.example.backend.image.repository;

import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.image.dto.TempImageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CustomImageRepository {
    TempImageDto findAll(Pageable pageable, List<String> tags, List<String> queries);
    List<ImageBoard> findBestBoards();
    Page<ImageBoard> findAll(Pageable pageable);
    List<ImageBoard> findAllForSearch(String sort, String[] queryList, String tag);
}
