package com.example.backend.user.repository;

import com.example.backend.myPage.dto.SubscribeUserDto;
import com.example.backend.user.domain.User;
import java.util.List;

public interface CustomSubscribeRepository {
    List<SubscribeUserDto> findAllNonSubscribingTargets(User user);
}
