package com.example.backend.blackList.repository;

import com.example.backend.blackList.domain.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {
    Optional<BlackList> findByToken(String token);
}
