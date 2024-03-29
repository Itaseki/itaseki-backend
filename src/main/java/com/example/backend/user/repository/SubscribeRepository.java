package com.example.backend.user.repository;

import com.example.backend.user.domain.Subscribe;
import com.example.backend.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long>, CustomSubscribeRepository {
    List<Subscribe> findAllByUser(User user);
    List<Subscribe> findAllBySubscribeTarget(User user);
    Optional<Subscribe> findByUserAndSubscribeTarget(User user, User subscribeTarget);
}
