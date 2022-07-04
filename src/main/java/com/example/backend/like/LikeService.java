package com.example.backend.like;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
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

    public void saveLike(Like like){
        likeRepository.save(like);
    }

    public Like findExistingLike(CommunityBoard communityBoard, User user){
        return likeRepository.findByUserAndCommunityBoard(user, communityBoard);
    }

    public Like findExistingLike(Video video, User user){
        return likeRepository.findByUserAndVideo(user,video);
    }

    public Like findExistingLike(ImageBoard imageBoard, User user){
        return likeRepository.findByUserAndImageBoard(user, imageBoard);
    }

    public Like findExistingLike(Playlist playlist, User user){
        return likeRepository.findByUserAndPlaylist(user,playlist).orElse(null);
    }
}
