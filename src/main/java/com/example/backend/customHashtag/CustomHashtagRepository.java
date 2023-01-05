package com.example.backend.customHashtag;

import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.image.domain.ImageBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomHashtagRepository extends JpaRepository<CustomHashtag, Long> {
    List<CustomHashtag> findByImageBoard_Id(Long imageBoardId);
}
