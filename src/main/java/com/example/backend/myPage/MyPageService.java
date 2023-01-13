package com.example.backend.myPage;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.repository.CommunityBoardRepository;
import com.example.backend.community.repository.CommunityCommentRepository;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.image.repository.ImageBoardRepository;
import com.example.backend.like.Like;
import com.example.backend.like.LikeRepository;
import com.example.backend.myPage.dto.DetailPlaylistDto;
import com.example.backend.myPage.dto.LikeDataDto;
import com.example.backend.myPage.dto.MyCommentDto;
import com.example.backend.myPage.dto.MyDataDto;
import com.example.backend.myPage.dto.MyPageCommunityDto;
import com.example.backend.myPage.dto.MyPageImageDto;
import com.example.backend.myPage.dto.MyPagePlaylistDto;
import com.example.backend.myPage.dto.MyPageVideoDto;
import com.example.backend.myPage.dto.MySubscribeDto;
import com.example.backend.myPage.dto.SubscribeUserDto;
import com.example.backend.myPage.dto.UserEditInfoDto;
import com.example.backend.myPage.dto.UserEditRequest;
import com.example.backend.myPage.dto.UserInfoDto;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.playlist.exception.PlaylistNotFoundException;
import com.example.backend.playlist.repository.PlaylistCommentRepository;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.s3Image.AwsS3Service;
import com.example.backend.user.domain.Subscribe;
import com.example.backend.user.domain.User;
import com.example.backend.user.repository.SubscribeRepository;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import com.example.backend.video.repository.VideoCommentRepository;
import com.example.backend.video.repository.VideoRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {
    private final PlaylistService playlistService;
    private final SubscribeRepository subscribeRepository;
    private final VideoRepository videoRepository;
    private final LikeRepository likeRepository;
    private final CommunityBoardRepository communityBoardRepository;
    private final ImageBoardRepository imageBoardRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final VideoCommentRepository videoCommentRepository;
    private final PlaylistCommentRepository playlistCommentRepository;
    private final AwsS3Service awsS3Service;
    private final static long RECOMMENDATION_LIMIT = 1L;
    private final static int SUBSCRIBE_SHOW_COUNT = 5;

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

    public MyDataDto getAllDataUploadedByUser(User user) {
        return new MyDataDto(findAllCommunityBoardByUser(user), findAllVideoByUser(user), findAllImageByUser(user), findAllCommentsByUser(user));
    }

    public List<MyPagePlaylistDto> findAllPlaylistByUser(User user) {
        return playlistService.findAllPlaylistByUser(user)
                .stream()
                .sorted(Comparator.comparing(Playlist::getCreatedTime))
                .map(playlist -> MyPagePlaylistDto.forMyPlaylist(playlist,
                                playlistService.getFirstThumbnailInPlaylist(playlist.getId()),
                                playlistService.findAllVideosInPlaylist(playlist.getId()).size()))
                .collect(Collectors.toList());
    }

    public List<MyPagePlaylistDto> findAllSavedPlaylist(User user) {
        return playlistService.findAllSavedPlaylistByUser(user)
                .stream()
                .map(UserSavedPlaylist::getPlaylist)
                .sorted(Comparator.comparing(Playlist::getCreatedTime))
                .map(playlist -> MyPagePlaylistDto.forSavedPlaylist(playlist,
                        playlistService.getFirstThumbnailInPlaylist(playlist.getId()),
                        playlistService.findAllVideosInPlaylist(playlist.getId()).size()))
                .collect(Collectors.toList());
    }

    public DetailPlaylistDto getMyPagePlaylistDetail(Long playlistId) {
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        if (playlist == null) {
            throw new PlaylistNotFoundException();
        }
        return DetailPlaylistDto.builder()
                .playlist(playlist)
                .videos(playlistService.findAllVideosInPlaylist(playlistId))
                .build();
    }

    public void editUserInfo(User user, UserEditRequest request, MultipartFile profileImage) {
        user.updateUserProfileInfo(request.getNickname(), request.getDescription(), awsS3Service.uploadFile(profileImage));
    }

    public UserEditInfoDto getUserInfoForEdit(User user) {
        return UserEditInfoDto.ofUser(user);
    }

    public MySubscribeDto getMyPageSubscribeInfo(User user) {
        return MySubscribeDto.builder()
                .mySubscribe(findAllSubscribingTargetsByUser(user))
                .recommendedSubscribe(recommendSubscribingTargets(user))
                .build();
    }


    public void saveSubscribe(User user, User target) {
        subscribeRepository.findByUserAndSubscribeTarget(user, target)
                .ifPresentOrElse(subscribe -> updateSubscribeInfo(subscribe.modifySubscribeStatus()),
                        () -> updateSubscribeInfo(createNewSubscribe(user, target)));
    }

    private void updateSubscribeInfo(Subscribe subscribe){
        subscribeRepository.save(subscribe);
    }

    private Subscribe createNewSubscribe(User user, User target) {
        return Subscribe.builder()
                .subscribeTarget(target)
                .lastModified(LocalDateTime.now())
                .user(user)
                .build();
    }


    private List<MyPageCommunityDto> findAllCommunityBoardByUser(User user) {
        return communityBoardRepository.findAllByUserOrderByCreatedTimeDesc(user)
                .stream()
                .filter(CommunityBoard::getStatus)
                .map(MyPageCommunityDto::of)
                .collect(Collectors.toList());
    }

    private List<MyPageImageDto> findAllImageByUser(User user) {
        return imageBoardRepository.findAllByUserOrderByCreatedTimeDesc(user)
                .stream()
                .filter(ImageBoard::getStatus)
                .map(MyPageImageDto::of)
                .collect(Collectors.toList());
    }

    private List<MyPageVideoDto> findAllVideoByUser(User user) {
        return videoRepository.findAllByUserOrderByCreatedTimeDesc(user)
                .stream()
                .filter(Video::getStatus)
                .map(MyPageVideoDto::of)
                .collect(Collectors.toList());
    }

    private List<Playlist> findAllPublicPlaylistByUser(User user) {
        return playlistService.findAllPublicPlaylistsByUserDesc(user);
    }

    // "user" 를 구독하는 사람들
    private List<User> findMySubscribers(User user) {
        return subscribeRepository.findAllBySubscribeTarget(user)
                .stream()
                .filter(Subscribe::getStatus)
                .map(Subscribe::getUser)
                .collect(Collectors.toList());
    }

    private List<SubscribeUserDto> findAllSubscribingTargetsByUser(User user) {
        return subscribeRepository.findAllByUser(user)
                .stream()
                .filter(Subscribe::getStatus)
                .sorted(Comparator.comparing(Subscribe::getLastModified).reversed())
                .limit(SUBSCRIBE_SHOW_COUNT)
                .map(Subscribe::getSubscribeTarget)
                .map(target -> SubscribeUserDto.ofUser(target, findMySubscribers(target).size()))
                .collect(Collectors.toList());
    }

    private List<SubscribeUserDto> recommendSubscribingTargets(User user) {
        List<SubscribeUserDto> recommendation = subscribeRepository.findAllNonSubscribingTargets(user, RECOMMENDATION_LIMIT);
        Collections.shuffle(recommendation);
        return recommendation.stream()
                .limit(SUBSCRIBE_SHOW_COUNT)
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

    private List<MyCommentDto> findAllCommentsByUser(User user) {
        return Stream.concat(Stream.concat(findAllCommunityCommentByUser(user), findAllPlaylistCommentByUser(user)), findAllVideoCommentByUser(user))
                .sorted(Comparator.comparing(MyCommentDto::getCreatedTime).reversed())
                .collect(Collectors.toList());
    }

    private Stream<MyCommentDto> findAllCommunityCommentByUser(User user) {
        return communityCommentRepository.findAllByUser(user)
                .stream()
                .filter(CommunityComment::getStatus)
                .map(MyCommentDto::ofCommunityBoard);
    }

    private Stream<MyCommentDto> findAllVideoCommentByUser(User user) {
        return videoCommentRepository.findAllByUser(user)
                .stream()
                .filter(VideoComment::getStatus)
                .map(MyCommentDto::ofVideo);
    }

    private Stream<MyCommentDto> findAllPlaylistCommentByUser(User user) {
        return playlistCommentRepository.findAllByUser(user)
                .stream()
                .filter(PlaylistComment::getStatus)
                .map(MyCommentDto::ofPlaylist);
    }
}
