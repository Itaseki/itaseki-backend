package com.example.backend.like;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public int saveLike(CommunityBoard communityBoard, User user){
        //존재하는지 찾아서, 없으면 새롭게 하나 추가해주고 있으면 status 를 반대로 바꿔주기
        Like like=findExistingLike(communityBoard,user);
        if(like==null){
            like=Like.builder().communityBoard(communityBoard).user(user).build();
        }
        else{
            like.modifyLikeStatus();
        }
        likeRepository.save(like);
        return getLikeCount(communityBoard);
    }

    public Like findExistingLike(CommunityBoard communityBoard, User user){
        return likeRepository.findByUserAndCommunityBoard(user, communityBoard);
    }

    //고민 중: OneToMany 로 양방향 매핑을 해서 CommunityBoard 에서 List 로 가져올지, 아니면 지금 처럼 findBy 로 like 객체에서 찾아올지
    public int getLikeCount(CommunityBoard communityBoard){
        List<Like> originLikes = likeRepository.findAllByCommunityBoard(communityBoard);
        List<Like> likes = originLikes.stream()
                .filter(like -> like.getLikeStatus().equals(true))
                .collect(Collectors.toList());
        return likes.size();
    }


}
