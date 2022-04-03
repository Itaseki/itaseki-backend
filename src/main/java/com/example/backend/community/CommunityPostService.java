package com.example.backend.community;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityPostService {
    private final CommunityPostRepository communityPostRepository;

    public void savePost(CommunityPost communityPost){
        communityPostRepository.save(communityPost);
    }

    public CommunityPost findPostById(Long id){
        Optional<CommunityPost> post = communityPostRepository.findById(id);
        return post.orElse(null);
    }

}
