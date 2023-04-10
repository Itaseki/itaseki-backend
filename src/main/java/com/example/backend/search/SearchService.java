package com.example.backend.search;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistVideo;
import com.example.backend.search.dto.SearchPlaylistPageableResponse;
import com.example.backend.search.dto.SearchPlaylistResponse;
import com.example.backend.search.dto.SearchVideoPageableResponse;
import com.example.backend.search.dto.SearchVideoResponse;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.video.domain.Video;
import com.example.backend.video.service.VideoService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final VideoService videoService;
    private final PlaylistService playlistService;

    public SearchVideoPageableResponse getVideoForSearch(String q, String tag, String series, Pageable pageable) {
        return findSearchResultForVideo(videoService.getAllVideoForSearch(q, tag, series, pageable));
    }

    public SearchPlaylistPageableResponse getPlaylistsForSearch(String q, String tag, String series, Pageable pageable) {
        return findSearchResultForPlaylist(playlistService.getAllPlaylistForSearch(q, pageable, tag, series), tag);
    }

    private SearchPlaylistPageableResponse findSearchResultForPlaylist(Page<Playlist> playlists, String tag) {
        return SearchPlaylistPageableResponse.of(mapPlaylistToResponse(playlists.getContent(), tag),
                playlists.getTotalPages());
    }

    private SearchVideoPageableResponse findSearchResultForVideo(Page<Video> videos) {
        return SearchVideoPageableResponse.of(updateTagsInVideo(videos.getContent()), videos.getTotalPages());
    }

    private List<SearchVideoResponse> updateTagsInVideo(List<Video> videos) {
        List<SearchVideoResponse> data = videos
                .stream()
                .map(SearchVideoResponse::fromEntity)
                .collect(Collectors.toList());

        data.forEach(video -> video.updateTags(
                videoService.getHashtagKeywordStringInVideo(videoService.findVideoEntityById(video.getId()))));

        return data;
    }

    private List<SearchPlaylistResponse> mapPlaylistToResponse(List<Playlist> playlists, String tag) {
        List<SearchPlaylistResponse> data = playlists.stream()
                .map(playlist -> SearchPlaylistResponse.fromPlaylistAndData(playlist,
                        playlistService.getFirstThumbnailInPlaylist(playlist.getId()),
                        playlistService.findAllVideosInPlaylist(playlist.getId()).size()))
                .collect(Collectors.toList());

        data.forEach(playlist -> playlist.updateTags(findTagsForPlaylistBySearchTag(tag, playlist.getId())));

        return data;
    }

    private List<String> findTagsForPlaylistBySearchTag(String searchTag, long playlistId) {
        Video videoContainingTag = playlistService.findPlaylistEntity(playlistId)
                .getVideos().stream()
                .map(PlaylistVideo::getVideo)
                .filter(video -> isVideoContainingSearchTag(searchTag, video))
                .findFirst()
                .orElse(null);
        // 검색된 태그를 가지는 영상의 해시태그를 반환한다.

        // 태그 검색이 아닌 경우, 플레이리스트 내부 첫 번쨰 영상의 태그를 반환한다
        if (videoContainingTag == null) {
            return findFirstVideoTagInPlaylist(playlistId);
        }

        return videoService.getHashtagKeywordStringInVideo(videoContainingTag);
    }

    private List<String> findFirstVideoTagInPlaylist(long playlistId) {
        PlaylistVideo playlistVideo = playlistService.findAllVideosInPlaylist(playlistId)
                .stream()
                .findFirst()
                .orElse(null);

        if (playlistVideo == null) {
            return Collections.emptyList();
        }

        return videoService.getHashtagKeywordStringInVideo(playlistVideo.getVideo());
    }

    private boolean isVideoContainingSearchTag(String searchTag, Video videoInPlaylist) {
        return videoService.getHashtagKeywordStringInVideo(videoInPlaylist).contains(searchTag);
    }

}
