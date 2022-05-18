package com.example.backend.like;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public void saveLike(Like like){
        //존재하는지 찾아서, 없으면 새롭게 하나 추가해주고 있으면 status 를 반대로 바꿔주기
        likeRepository.save(like);
    }

    public Like findExistingLike(CommunityBoard communityBoard, User user){
        return likeRepository.findByUserAndCommunityBoard(user, communityBoard);
    }


}
