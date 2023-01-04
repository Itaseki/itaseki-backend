package com.example.backend.myPage;

import com.example.backend.like.Like;
import com.example.backend.like.LikeRepository;
import com.example.backend.myPage.dto.LikeDataDto;
import com.example.backend.myPage.dto.MyPageCommunityDto;
import com.example.backend.myPage.dto.MyPageImageDto;
import com.example.backend.myPage.dto.MyPageVideoDto;
import com.example.backend.myPage.dto.UserInfoDto;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.user.domain.Subscribe;
import com.example.backend.user.domain.User;
import com.example.backend.user.repository.SubscribeRepository;
import com.example.backend.video.repository.VideoRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final PlaylistService playlistService;
    private final SubscribeRepository subscribeRepository;
    private final VideoRepository videoRepository;
    private final LikeRepository likeRepository;

    public UserInfoDto findUserBasicInformation(User user) {
        return UserInfoDto.fromUserAndDetail(user,
                findAllPublicPlaylistByUser(user).size(),
                findMySubscribers(user).size(),
                getUserReportedCount(user));
    }

    public LikeDataDto getAllLikedData(User user) {
        List<Like> likedData = findAllLikedByUser(user);
        return new LikeDataDto(findAllLikedVideos(likedData), findAllLikedCommunityBoards(likedData),
                findAllLikedImages(likedData));
    }

    private List<Playlist> findAllPublicPlaylistByUser(User user) {
        return playlistService.findAllPublicPlaylistsByUserDesc(user);
    }

    private List<User> findMySubscribers(User user) {
        return subscribeRepository.findAllBySubscribeTarget(user)
                .stream()
                .filter(Subscribe::getStatus)
                .map(Subscribe::getUser)
                .collect(Collectors.toList());
    }

    private int getUserReportedCount(User user) {
        return user.getUserReportCount();
    }

    private List<MyPageImageDto> findAllLikedImages(List<Like> likes) {
        return likes.stream()
                .filter(like -> like.getImageBoard() != null)
                .map(like -> MyPageImageDto.of(like.getImageBoard()))
                .collect(Collectors.toList());
    }

    private List<MyPageCommunityDto> findAllLikedCommunityBoards(List<Like> likes) {
        return likes.stream()
                .filter(like -> like.getCommunityBoard() != null)
                .map(like -> MyPageCommunityDto.of(like.getCommunityBoard()))
                .collect(Collectors.toList());
    }

    private List<MyPageVideoDto> findAllLikedVideos(List<Like> likes) {
        return likes.stream()
                .filter(like -> like.getVideo() != null)
                .map(like -> MyPageVideoDto.of(like.getVideo()))
                .collect(Collectors.toList());
    }

    private List<Like> findAllLikedByUser(User user) {
        return likeRepository.findAllByUser(user)
                .stream()
                .filter(Like::getLikeStatus)
                .collect(Collectors.toList());
    }
}
