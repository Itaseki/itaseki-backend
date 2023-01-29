package com.example.backend.video.service;

import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.customHashtag.CustomHashtagRepository;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.user.domain.User;
import com.example.backend.video.VideoUrlType;
import com.example.backend.video.domain.*;
import com.example.backend.video.dto.*;
import com.example.backend.video.exception.NoSuchVideoException;
import com.example.backend.video.repository.*;
import java.util.Collections;
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

import static java.lang.Integer.parseInt;

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
    private final int BEST_VIDEO_COUNT = 4;

    public void saveVideo(VideoDto videoDto, User user) {
        List<Long> playlists = videoDto.getPlaylists();
        Video video = videoDto.toEntityWithUserAndSeries(user, getSeries(videoDto.getSeries()));
        video.setVideoRuntime(toHourMinSec(videoDto.getRuntime()));
        saveVideoHashtag(video, getHashtagsByIds(videoDto.getHashtags()));
        saveVideoKeywords(videoDto.getKeywords(), video);
        Video savedVideo = videoRepository.save(video);

        if (playlists != null && !playlists.isEmpty()) {
            playlists.stream()
                    .map(playlistService::findPlaylistEntity)
                    .forEach(playlist -> playlistService.addVideoToPlaylist(savedVideo.getId(), playlist));

        }
    }


    private List<Hashtag> getHashtagsByIds(List<Long> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return ids.stream().map(id -> hashtagRepository.findById(id).get()).collect(Collectors.toList());
    }

    private void saveVideoHashtag(Video video, List<Hashtag> hashtags) {
        int order = 1;
        for (Hashtag hashtag : hashtags) {
            VideoHashtag videoHashtag = VideoHashtag.builder().video(video).hashtag(hashtag).order(order++).build();
            videoTagRepository.save(videoHashtag);
        }
    }

    private void saveVideoKeywords(List<String> keywords, Video video) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }
        int order = 1;
        for (String keyword : keywords) {
            customHashtagRepository.save(CustomHashtag.builder().video(video).name(keyword).order(order++).build());
        }
    }

    private Series getSeries(Long id) {
        Optional<Series> series = seriesRepository.findById(id);
        return series.orElse(null);
    }

    private int[] toHourMinSec(String runtime) {
        String[] strings = runtime.split(":");
        int hour, min, sec;
        switch (strings.length) {
            case 3:
                hour = parseInt(strings[0]);
                min = parseInt(strings[1]);
                sec = parseInt(strings[2]);
                break;
            case 2:
                hour = 0;
                min = parseInt(strings[0]);
                sec = parseInt(strings[1]);
                break;
            default:
                hour = 0;
                min = 0;
                sec = 0;
        }
        return new int[]{hour, min, sec};
    }

    public String checkVideoUrlExistence(String url) {
        if (videoRepository.findByVideoUrlContains(VideoUrlType.extractVideoId(url)).isPresent()) {
            return "등록 불가능";
        }
        return "등록 가능";
    }

    private List<InnerInfoResponse> findAllSeries() {
        return seriesRepository.findAll().stream()
                .map(InnerInfoResponse::new)
                .collect(Collectors.toList());
    }

    private List<InnerInfoResponse> findAllHashtags() {
        return hashtagRepository.findAll()
                .stream()
                .map(InnerInfoResponse::new)
                .collect(Collectors.toList());
    }

    private List<InnerInfoResponse> findAllPlayListsOfUser(User user) {
        return playlistService.findAllPlaylistByUser(user)
                .stream()
                .map(InnerInfoResponse::new)
                .collect(Collectors.toList());
    }

    public VideoUploadInfoResponse getPreInfoForVideoUpload(User user) {
        return VideoUploadInfoResponse.toInfoResponse(findAllSeries(), findAllHashtags(), findAllPlayListsOfUser(user));
    }

    public Video findVideoEntityById(Long id) {
        Optional<Video> video = videoRepository.findById(id);
        if (video.isPresent() && video.get().getStatus()) {
            return video.get();
        }
        throw new NoSuchVideoException();
    }

    public List<String> getHashtagKeywordStringInVideo(Video video) {
        List<VideoHashtag> videoHashtags = video.getVideoHashtags();
        List<CustomHashtag> customHashtags = video.getCustomHashtags();
        return Stream.concat(videoHashtags.stream().map(videoHashtag -> videoHashtag.getHashtag().getHashtagName())
                        , customHashtags.stream().map(CustomHashtag::getCustomHashtagName))
                .collect(Collectors.toList());
    }

    public DetailVideoResponse getDetailVideoResponse(Video video, Long loginId) {
        User videoWriter = video.getUser();
        Long videoWriterId = videoWriter.getUserId();
        List<VideoComment> parentComments = video.getVideoComments()
                .stream()
                .filter(comment -> comment.getIsParentComment().equals(true)) //부모댓글만 넘김
                .collect(Collectors.toList());
        List<VideoCommentsResponse> videoCommentResponses = videoCommentService.getVideoCommentResponses(parentComments,
                loginId, videoWriterId);
        return DetailVideoResponse.fromEntity(video, videoCommentResponses, loginId,
                getHashtagKeywordStringInVideo(video));
    }

    public void updateVideoViewCount(Video video) {
        video.updateVideoViewCount();
        videoRepository.save(video);
    }

    public AllVideoResponseWithPageCount getAllVideosResponse(Pageable pageable) {
        TempVideoDto tempDto = videoRepository.findAllByPageable(pageable);
        List<AllVideoResponse> allVideoResponses = toAllResponse(tempDto.getVideos());
        return new AllVideoResponseWithPageCount(allVideoResponses, getTotalPageCount(tempDto.getTotalCount()));
    }

    public Page<Video> getAllVideoForSearch(String q, String tag, Pageable pageable) {
        List<String> queries = Arrays.stream(q.split(" ")).collect(Collectors.toList());
        return videoRepository.findAllForSearch(tag, queries, pageable);
    }

    private int getTotalPageCount(long pages) {
        if (pages <= 8) {
            return 1;
        }
        return (int) (1 + Math.ceil((pages - 8) / (double) 12));
    }

    private List<AllVideoResponse> toAllResponse(List<Video> videos) {
        return videos.stream()
                .map(AllVideoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AllVideoResponse> getBestVideos() {
        return toAllResponse(videoRepository.findBestVideos(BEST_VIDEO_COUNT));

    }

    public void deleteVideo(Video video) {
        video.setStatus(false);
        videoRepository.save(video);
        video.getPlaylistVideos()
                .forEach(pv -> playlistService.deleteVideoInPlaylist(video, pv.getPlaylist().getId()));
    }

    public List<InnerInfoResponse> findSeriesNameByQuery(String q) {
        return seriesRepository.findBySeriesNameContainsIgnoreCase(q)
                .stream()
                .map(InnerInfoResponse::new)
                .collect(Collectors.toList());
    }

    public List<Video> findVideoContainingTitle(String searchTitle, String order) {
        //order: id 라면? 최신순 / likeCount 라면? 좋아요순
        return videoRepository.findTitleLike(searchTitle, order);
    }

}
