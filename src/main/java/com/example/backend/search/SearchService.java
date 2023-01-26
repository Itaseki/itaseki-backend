package com.example.backend.search;

import com.example.backend.community.dto.AllCommunityBoardsResponse;
import com.example.backend.community.service.CommunityBoardService;
import com.example.backend.image.repository.ImageBoardRepository;
import com.example.backend.main.dto.MainImageResponse;
import com.example.backend.search.dto.SearchPageableResponse;
import com.example.backend.search.dto.SearchVideoResponse;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.user.service.UserService;
import com.example.backend.video.domain.Video;
import com.example.backend.video.service.VideoService;
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

    public SearchPageableResponse getVideoForSearch(String q, String tag, Pageable pageable) {
        return findSearchResultForVideo(videoService.getAllVideoForSearch(q, tag, pageable));
    }

    public List<AllPlaylistsResponse> getPlaylistsForSearch(String q, String tag, Pageable pageable) {
        return playlistService.getAllPlaylistForSearch(q, pageable, tag);
    }

    private SearchPageableResponse findSearchResultForVideo(Page<Video> videos) {
        return SearchPageableResponse.of(updateTagsInVideo(videos.getContent()), videos.getTotalPages());
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

}
