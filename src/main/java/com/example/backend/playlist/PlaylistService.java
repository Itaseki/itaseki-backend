package com.example.backend.playlist;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistVideo;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.playlist.dto.MyPlaylistResponse;
import com.example.backend.playlist.dto.NewEmptyPlaylistDto;
import com.example.backend.playlist.repository.PlaylistRepository;
import com.example.backend.playlist.repository.PlaylistVideoRepository;
import com.example.backend.playlist.repository.UserSavedPlaylistRepository;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository pvRepository;
    private final VideoService videoService;
    private final UserSavedPlaylistRepository savedPlaylistRepository;

    public MyPlaylistResponse saveEmptyPlaylist(NewEmptyPlaylistDto emptyDto, User user){
        String title = emptyDto.getTitle();
        Boolean isPublic=emptyDto.getIsPublic();
        Playlist playlist = Playlist.builder()
                .title(title).user(user)
                .isPublic(isPublic).now(LocalDateTime.now())
                .build();
        Playlist saved = playlistRepository.save(playlist);
        return MyPlaylistResponse.fromEntity(saved);
    }

    public void addVideoToPlaylist(Long videoId, Long playlistId){
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        Video video = videoService.findVideoEntityById(videoId);
        Integer lastVideoOrder = pvRepository.findLastVideoOrder(playlist);
        PlaylistVideo playlistVideo = PlaylistVideo.builder()
                .playlist(playlist)
                .video(video)
                .order(lastVideoOrder != null ? ++lastVideoOrder : 1)
                .build();
        pvRepository.save(playlistVideo);
    }

    private List<Playlist> findAllUserPlaylist(User user){
        return playlistRepository.findAllByUser(user)
                .stream()
                .filter(Playlist::getStatus)
                .collect(Collectors.toList());
    }

    public List<MyPlaylistResponse> getMyPlaylist(User user){
        return findAllUserPlaylist(user)
                .stream()
                .map(MyPlaylistResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public UserSavedPlaylist findExistingSavedPlaylist(User user, Playlist playlist){
        return savedPlaylistRepository.findByUserAndPlaylist(user, playlist);
    }

    public Playlist findPlaylistEntity(Long playlistId){
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        if(playlist==null)
            return null;
        if(playlist.getStatus())
            return playlist;
        return null;
    }

    public void userPlaylistSave(Playlist playlist, User user){
        UserSavedPlaylist userSavedPlaylist = UserSavedPlaylist.builder()
                .playlist(playlist)
                .user(user)
                .status(true)
                .build();
        savedPlaylistRepository.save(userSavedPlaylist);
    }

}
