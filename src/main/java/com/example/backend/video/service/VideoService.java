package com.example.backend.video.service;

import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.customHashtag.CustomHashtagRepository;
import com.example.backend.like.LikeService;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.report.ReportService;
import com.example.backend.user.domain.User;
import com.example.backend.video.VideoUrlType;
import com.example.backend.video.domain.*;
import com.example.backend.video.dto.*;
import com.example.backend.video.exception.NoSuchHashtagException;
import com.example.backend.video.exception.NoSuchSeriesException;
import com.example.backend.video.exception.NoSuchVideoException;
import com.example.backend.video.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final HashtagRepository hashtagRepository;
    private final SeriesRepository seriesRepository;
    private final VideoHashtagRepository videoTagRepository;
    private final CustomHashtagRepository customHashtagRepository;
    private final VideoCommentService videoCommentService;
    private final PlaylistService playlistService;
    private final LikeService likeService;
    private final ReportService reportService;
    private final int BEST_VIDEO_COUNT = 4;

    public void saveVideo(VideoPostRequest videoPostRequest, User user) {
        Video video = videoPostRequest.toEntityWithUserAndSeries(user, findSeriesById(videoPostRequest.getSeries()));
        saveVideoHashtag(video, getHashtagsByIds(videoPostRequest.getHashtags()));
        saveVideoKeywords(videoPostRequest.getKeywords(), video);
        addUploadedVideoToPlaylist(videoPostRequest.getPlaylists(), videoRepository.save(video).getId());
    }

    public int updateLikeOnVideo(long videoId, User user) {
        return likeService.findLikeAndUpdateCount(findVideoEntityById(videoId), user);
    }

    public boolean isVideoAlreadyReported(Video video, User user) {
        if (reportService.isAlreadyReported(user, video)) {
            return true;
        }
        reportService.createNewReport(video, user);
        return false;
    }

    public boolean isVideoDeletedByReport(Video video) {
        if (video.isReportCountOverLimit()) {
            deleteVideo(video);
            return true;
        }
        return false;
    }

    public String checkVideoUrlExistence(String url) {
        List<Video> videos = videoRepository.findAllByVideoUrlContains(
            VideoUrlType.extractVideoId(url));
        boolean isExist = videos.stream()
            .anyMatch(Video::getStatus);
        if (isExist) {
            return "등록 불가능";
        }
        return "등록 가능";
    }

    public VideoUploadInfoResponse getPreInfoForVideoUpload(User user) {
        return VideoUploadInfoResponse.toInfoResponse(seriesRepository.findAll(),
                hashtagRepository.findAll(),
                playlistService.findAllPlaylistByUser(user));
    }

    public Video findVideoEntityById(Long id) {
        Optional<Video> video = videoRepository.findById(id);
        if (video.isPresent() && video.get().getStatus()) {
            return video.get();
        }
        throw new NoSuchVideoException();
    }

    public List<String> getHashtagKeywordStringInVideo(Video video) {
        return Stream.concat(video.getVideoHashtags()
                                .stream()
                                .map(videoHashtag -> videoHashtag.getHashtag().getHashtagName())
                        , video.getCustomHashtags()
                                .stream()
                                .map(CustomHashtag::getCustomHashtagName))
                .collect(Collectors.toList());
    }

    public DetailVideoResponse getDetailVideoResponse(Video video, Long loginId) {
        updateVideoViewCount(video);
        return DetailVideoResponse.fromEntity(video,
                videoCommentService.getVideoCommentResponses(video, loginId, video.getUser().getUserId()),
                loginId, getHashtagKeywordStringInVideo(video));
    }

    public AllVideoResponseWithPageCount getAllVideosResponse(Pageable pageable) {
        return AllVideoResponseWithPageCount.fromAllVideoQuery(videoRepository.findAllByPageable(pageable));
    }

    public Page<Video> getAllVideoForSearch(String q, String tag, String series, Pageable pageable) {
        List<String> queries = Arrays.stream(q.split(" ")).collect(Collectors.toList());
        return videoRepository.findAllForSearch(tag, queries, series, pageable);
    }

    public List<AllVideoResponse> getBestVideos() {
        return videoRepository.findBestVideos(BEST_VIDEO_COUNT)
                .stream()
                .map(AllVideoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteVideo(Video video) {
        video.setStatus(false);
        videoRepository.save(video);
        video.getPlaylistVideos()
                .forEach(videoPlaylistPair ->
                        playlistService.deleteVideoInPlaylist(video, videoPlaylistPair.getPlaylist()));
    }

    public List<NameIdResponse> findSeriesNameByQuery(String q) {
        return seriesRepository.findBySeriesNameContainsIgnoreCase(q)
                .stream()
                .map(NameIdResponse::new)
                .collect(Collectors.toList());
    }

    public List<Video> findVideoContainingTitle(String searchTitle, String order) {
        return videoRepository.findTitleLike(searchTitle, order);
    }

    private void addUploadedVideoToPlaylist(List<Long> playlists, long videoId) {
        playlists.stream()
                .map(playlistService::findPlaylistEntity)
                .forEach(playlist -> playlistService.addVideoToPlaylist(videoId, playlist));
    }

    private List<Hashtag> getHashtagsByIds(List<Long> ids) {
        return ids.stream()
                .map(this::findHashtagById)
                .collect(Collectors.toList());
    }

    private void saveVideoHashtag(Video video, List<Hashtag> hashtags) {
        int order = 1;
        for (Hashtag hashtag : hashtags) {
            videoTagRepository.save(VideoHashtag
                    .builder()
                    .video(video)
                    .hashtag(hashtag)
                    .order(order++)
                    .build());
        }
    }

    private void saveVideoKeywords(List<String> keywords, Video video) {
        int order = 1;
        for (String keyword : keywords) {
            customHashtagRepository.save(CustomHashtag
                    .builder()
                    .video(video)
                    .name(keyword)
                    .order(order++)
                    .build());
        }
    }

    private Series findSeriesById(Long id) {
        return seriesRepository.findById(id)
                .orElseThrow(NoSuchSeriesException::new);
    }

    private Hashtag findHashtagById(long id) {
        return hashtagRepository.findById(id)
                .orElseThrow(NoSuchHashtagException::new);
    }

    private void updateVideoViewCount(Video video) {
        video.updateVideoViewCount();
        videoRepository.save(video);
    }
}
