package com.example.backend.blackList.service;

import com.example.backend.blackList.domain.BlackList;
import com.example.backend.blackList.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlackListService {

    private final BlackListRepository blackListRepository;

    public BlackList getBlackListEntity(String token){
        Optional<BlackList> blackList = blackListRepository.findByToken(token);
        return blackList.orElse(null);
    }

    public void saveBlackList(BlackList blackList){
        blackListRepository.save(blackList);
    }
}
