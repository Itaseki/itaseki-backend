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
import com.example.backend.myPage.dto.UserInfoDto;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.playlist.exception.PlaylistNotFoundException;
import com.example.backend.playlist.repository.PlaylistCommentRepository;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.user.domain.Subscribe;
import com.example.backend.user.domain.User;
import com.example.backend.user.repository.SubscribeRepository;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import com.example.backend.video.repository.VideoCommentRepository;
import com.example.backend.video.repository.VideoRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
