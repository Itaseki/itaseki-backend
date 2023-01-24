package com.example.backend.myPage;

import com.example.backend.myPage.dto.MyCommentDto;
import com.example.backend.myPage.dto.DetailDataResponse;
import com.example.backend.myPage.dto.MyPagePageableResponse;
import com.example.backend.myPage.dto.UserInfoResponse;
import com.example.backend.myPage.dto.UserEditRequest;
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
        return toUserUploadedPlaylistResponse(findAllPlaylistByUser(user), pageable);
    }

    public MyPagePageableResponse findVideosForMyPage(User user, Pageable pageable) {
        return toUserUploadedVideoResponse(findAllVideoByUser(user), pageable);
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

//    private int calculateOffset(int page) {
//        return page * PAGE_SIZE;
//    }

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

    public void editUserInfo(User user, UserEditRequest request, MultipartFile profileImage) {
        user.updateUserProfileInfo(request.getNickname(), request.getDescription(), awsS3Service.uploadFile(profileImage));
    }

    public String deleteUser(User user) {
        user.deleteUser();
        return "삭제 성공";
    }

    private List<MyCommentDto> findAllCommentsByUser(User user) {
        return Stream.concat(findAllPlaylistCommentByUser(user), findAllVideoCommentByUser(user))
                .sorted(Comparator.comparing(MyCommentDto::getCreatedTime).reversed())
                .collect(Collectors.toList());
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
