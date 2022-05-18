package com.example.backend.image.repository;

import com.example.backend.image.domain.ImageBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageBoardRepository extends JpaRepository<ImageBoard,Long>, CustomImageRepository {
    Page<ImageBoard> findAll(Pageable pageable);
}
