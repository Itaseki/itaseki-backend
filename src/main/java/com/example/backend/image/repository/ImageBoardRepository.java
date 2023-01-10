package com.example.backend.image.repository;

import com.example.backend.image.domain.ImageBoard;
import com.example.backend.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageBoardRepository extends JpaRepository<ImageBoard,Long>, CustomImageRepository {
    Page<ImageBoard> findAll(Pageable pageable);
    List<ImageBoard> findAllByUserOrderByCreatedTimeDesc(User user);
}
