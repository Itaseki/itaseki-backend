package com.example.backend.customHashtag;

import com.example.backend.customHashtag.CustomHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomHashtagRepository extends JpaRepository<CustomHashtag, Long> {
}
