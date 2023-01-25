package com.example.backend.myPage;

import com.amazonaws.util.StringUtils;
import com.example.backend.globalexception.WrongParamException;
import com.example.backend.myPage.dto.DetailCommentResponse;
import com.example.backend.myPage.dto.DetailDataResponse;
import com.example.backend.myPage.dto.MyPageCommentPageableResponse;
import com.example.backend.myPage.dto.MyPagePageableResponse;
import com.example.backend.myPage.dto.UserInfoResponse;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.playlist.repository.PlaylistCommentRepository;
import com.example.backend.playlist.repository.PlaylistRepository;
import com.example.backend.playlist.repository.PlaylistVideoRepository;
import com.example.backend.playlist.repository.UserSavedPlaylistRepository;
import com.example.backend.s3Image.AwsS3Service;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import com.example.backend.video.repository.VideoCommentRepository;
import com.example.backend.video.repository.VideoRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {
    private final VideoRepository videoRepository;
    private final VideoCommentRepository videoCommentRepository;
    private final PlaylistCommentRepository playlistCommentRepository;
    private final PlaylistRepository playlistRepository;
    private final UserSavedPlaylistRepository savedPlaylistRepository;
    private final PlaylistVideoRepository pvRepository;
    private final AwsS3Service awsS3Service;

    public UserInfoResponse findUserBasicInformation(User user) {
        return UserInfoResponse.ofUser(user);
    }

    public MyPagePageableResponse findPlaylistsForMyPage(User user, String type, Pageable pageable) {
        if (type.equals("saved")) {
            return toSavedPlaylistResponse(findAllSavedPlaylist(user), pageable);
        }
        if (type.equals("my")) {
            return toUserUploadedPlaylistResponse(findAllPlaylistByUser(user), pageable);
        }
        throw new WrongParamException();
    }

    public MyPagePageableResponse findVideosForMyPage(User user, Pageable pageable) {
        return toUserUploadedVideoResponse(findAllVideoByUser(user), pageable);
    }

    public MyPageCommentPageableResponse findCommentsForMyPage(User user, Pageable pageable) {
        return toUserUploadedCommentResponse(findAllCommentsByUser(user), pageable);
    }

    public String updateProfileImage(User user, MultipartFile file) {
        return user.updateProfileImage(awsS3Service.uploadFile(file));
    }

    public String updateNickname(User user, String nickname) {
        if (StringUtils.isNullOrEmpty(nickname)) {
            return "";
        }
        return user.updateNickname(nickname);
    }

    public String deleteUser(User user) {
        user.deleteUser();
        return "삭제 성공";
    }

    private List<Playlist> findAllPlaylistByUser(User user) {
        return playlistRepository.findAllByUserAndStatusOrderByCreatedTimeDesc(user, true);
    }

    private List<Playlist> findAllSavedPlaylist(User user) {
        return savedPlaylistRepository.findAllByUserAndStatusOrderByCreatedTimeDesc(user, true)
                .stream()
                .map(UserSavedPlaylist::getPlaylist)
                .filter(Playlist::getIsPublic)
                .collect(Collectors.toList());
    }

    private List<Video> findAllVideoByUser(User user) {
        return videoRepository.findAllByUserOrderByCreatedTimeDesc(user)
                .stream()
                .filter(Video::getStatus)
                .collect(Collectors.toList());
    }

    private int calculateTotalPage(double totalCount, int pageSize) {
        return (int) Math.ceil(totalCount / pageSize);
    }

    private MyPagePageableResponse toUserUploadedPlaylistResponse(List<Playlist> playlists, Pageable pageable) {
        return MyPagePageableResponse.of(playlists.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(playlist -> DetailDataResponse.forMyPlaylist(playlist, findFirstThumbnail(playlist)))
                .collect(Collectors.toList()), calculateTotalPage(playlists.size(), pageable.getPageSize()));
    }

    private MyPagePageableResponse toSavedPlaylistResponse(List<Playlist> playlists, Pageable pageable) {
        return MyPagePageableResponse.of(playlists.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(playlist -> DetailDataResponse.forSavedPlaylist(playlist, findFirstThumbnail(playlist)))
                .collect(Collectors.toList()), calculateTotalPage(playlists.size(), pageable.getPageSize()));
    }

    private MyPagePageableResponse toUserUploadedVideoResponse(List<Video> videos, Pageable pageable) {
        return MyPagePageableResponse.of(videos.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(DetailDataResponse::forMyVideo)
                .collect(Collectors.toList()), calculateTotalPage(videos.size(), pageable.getPageSize()));
    }

    private String findFirstThumbnail(Playlist playlist) {
        return pvRepository.findFirstThumbnailUrl(playlist);
    }

    private MyPageCommentPageableResponse toUserUploadedCommentResponse(List<DetailCommentResponse> comments, Pageable pageable) {
        return MyPageCommentPageableResponse.of(comments.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList()), calculateTotalPage(comments.size(), pageable.getPageSize()));
    }

    private List<DetailCommentResponse> findAllCommentsByUser(User user) {
        return Stream.concat(findAllPlaylistCommentByUser(user), findAllVideoCommentByUser(user))
                .sorted(Comparator.comparing(DetailCommentResponse::getCreatedTime).reversed())
                .collect(Collectors.toList());
    }

    private Stream<DetailCommentResponse> findAllVideoCommentByUser(User user) {
        return videoCommentRepository.findAllByUser(user)
                .stream()
                .filter(VideoComment::getStatus)
                .map(DetailCommentResponse::ofVideo);
    }

    private Stream<DetailCommentResponse> findAllPlaylistCommentByUser(User user) {
        return playlistCommentRepository.findAllByUser(user)
                .stream()
                .filter(PlaylistComment::getStatus)
                .map(DetailCommentResponse::ofPlaylist);
    }
}
