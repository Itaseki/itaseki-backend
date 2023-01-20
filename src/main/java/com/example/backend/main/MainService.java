package com.example.backend.main;

import com.example.backend.community.service.CommunityBoardService;
import com.example.backend.image.repository.ImageBoardRepository;
import com.example.backend.main.dto.MainCommunityResponse;
import com.example.backend.main.dto.MainImageResponse;
import com.example.backend.main.dto.MainUserResponse;
import com.example.backend.main.dto.MainVideoResponse;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {
    private final UserService userService;
    private final CommunityBoardService communityService;
    private final ImageBoardRepository imageRepository;
    private final VideoService videoService;
    private final PlaylistService playlistService;

    public List<MainCommunityResponse> getCommunityForMain(){
        return communityService.getBestResponseOfCommunityBoard()
                .stream()
                .map(MainCommunityResponse::fromAllResponse)
                .limit(4)
                .collect(Collectors.toList());
    }

    public List<MainImageResponse> getImageForMain(){
        return imageRepository.findBestBoards()
                .stream()
                .map(MainImageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<MainVideoResponse> getVideoForMain(){
        List<MainVideoResponse> responses = videoService.getBestVideos()
                .stream()
                .map(MainVideoResponse::fromAllResponse)
                .collect(Collectors.toList());

        responses.forEach(video -> video.updateTags(
                        videoService.getHashtagKeywordStringInVideo(videoService.findVideoEntityById(video.getId()))));

        return responses;
    }

    public List<AllPlaylistsResponse> getPlaylistsForMain(){
        return playlistService.getBestPlaylistsResponse();
    }

    public MainUserResponse getUserProfileForMain(Long loginId){
        User user = userService.findUserById(loginId);
        return MainUserResponse.fromEntity(user);
    }
}
