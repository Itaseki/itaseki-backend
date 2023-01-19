package com.example.backend.search;

import com.example.backend.community.dto.AllCommunityBoardsResponse;
import com.example.backend.community.service.CommunityBoardService;
import com.example.backend.image.repository.ImageBoardRepository;
import com.example.backend.main.dto.MainImageResponse;
import com.example.backend.search.dto.SearchVideoResponse;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.user.service.UserService;
import com.example.backend.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final CommunityBoardService communityService;
    private final ImageBoardRepository imageRepository;
    private final VideoService videoService;
    private final PlaylistService playlistService;

    public List<AllCommunityBoardsResponse> getCommunityForSearch(String q, String sort){
            return communityService.getSearchedCommunityBoards(q,sort);
    }

    public List<MainImageResponse> getImageForSearch(String query, String tag, String sort){
        String[] queryList = null;
        if(query!=null){
            queryList = query.split(" ");
        }
        return imageRepository.findAllForSearch(sort,queryList,tag)
                .stream()
                .map(MainImageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SearchVideoResponse> getVideoForSearch(String q, String tag, String nickname, String sort){
        List<SearchVideoResponse> responses = videoService.getAllVideoForSearch(q,tag,nickname,sort)
                .stream()
                .map(SearchVideoResponse::fromAllResponse)
                .collect(Collectors.toList());

        responses
                .forEach(r->r.updateTags(videoService.getHashtagsStringByVideoId(r.getId())));

        return responses;
    }

    public List<AllPlaylistsResponse> getPlaylistsForSearch(String sort, String q, String nickname){
        return playlistService.getAllPlaylistForSearch(q,sort,nickname);
    }

}
