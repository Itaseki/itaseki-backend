package com.example.backend.like;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public void saveLike(Like like){
        likeRepository.save(like);
    }

    public Like findExistingLike(CommunityBoard communityBoard, User user){
        return likeRepository.findByUserAndCommunityBoard(user, communityBoard);
    }

    public Like findExistingLike(Video video, User user){
        return likeRepository.findByUserAndVideo(user,video);
    }


}
