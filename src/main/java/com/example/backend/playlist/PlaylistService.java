package com.example.backend.playlist;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistVideo;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.playlist.dto.*;
import com.example.backend.playlist.repository.PlaylistRepository;
import com.example.backend.playlist.repository.PlaylistVideoRepository;
import com.example.backend.playlist.repository.UserSavedPlaylistRepository;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.repository.VideoRepository;
import com.example.backend.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository pvRepository;
    private final VideoRepository videoRepository;
    private final UserSavedPlaylistRepository savedPlaylistRepository;

    private Video findVideoEntityById(Long videoId){
        Optional<Video> video = videoRepository.findById(videoId);
        if(video.isPresent()&&video.get().getStatus()){
            return video.get();
        }
        return null;
    }

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

    public void addVideoToPlaylist(Long videoId, Playlist playlist){
        Video video = this.findVideoEntityById(videoId);
        Integer lastVideoOrder = pvRepository.findLastVideoOrder(playlist);
        PlaylistVideo playlistVideo = PlaylistVideo.builder()
                .playlist(playlist)
                .video(video)
                .order(lastVideoOrder != null ? ++lastVideoOrder : 1)
                .build();
        pvRepository.save(playlistVideo);
    }

    public Boolean checkVideoPlaylistExistence(Long videoId, Playlist playlist){
        Video video = this.findVideoEntityById(videoId);
        return findExistingPlaylistVideo(video,playlist)!=null;
    }

    private PlaylistVideo findExistingPlaylistVideo(Video video, Playlist playlist){
        PlaylistVideo playlistVideo = pvRepository.findByVideoAndPlaylist(video, playlist).orElse(null);
        if(playlistVideo!=null&&playlistVideo.getStatus())
            return playlistVideo;
        return null;
    }

    private List<Playlist> findAllPlaylistByUser(User user){
        return playlistRepository.findAllByUser(user)
                .stream()
                .filter(Playlist::getStatus)
                .collect(Collectors.toList());
    }

    public List<MyPlaylistResponse> getMyPlaylist(User user){
        return findAllPlaylistByUser(user)
                .stream()
                .map(MyPlaylistResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public UserSavedPlaylist findExistingSavedPlaylist(User user, Playlist playlist){
        UserSavedPlaylist savedPlaylist = savedPlaylistRepository.findByUserAndPlaylist(user, playlist);
        if(savedPlaylist!=null)
            return savedPlaylist.getStatus()?savedPlaylist:null;
        return null;
    }

    public Playlist findPlaylistEntity(Long playlistId){
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        if(playlist==null)
            return null;
        if(playlist.getStatus())
            return playlist;
        return null;
    }

    public void saveOthersPlaylist(Playlist playlist, User user){
        UserSavedPlaylist userSavedPlaylist = UserSavedPlaylist.builder()
                .playlist(playlist)
                .user(user)
                .status(true)
                .build();
        savedPlaylistRepository.save(userSavedPlaylist); //saveCount 도 변경
        playlist.updateSaveCount();
    }

    private List<UserSavedPlaylist> findAllSavedPlaylistByUser(User user){
        return savedPlaylistRepository.findAllByUser(user)
                .stream()
                .filter(UserSavedPlaylist::getStatus)
                .collect(Collectors.toList());
    }

    public List<PlaylistTitleResponse> getUserSavedPlaylists(User user){
        return findAllSavedPlaylistByUser(user)
                .stream()
                .map(PlaylistTitleResponse::fromSavedPlaylist)
                .collect(Collectors.toList());
    }

    public Boolean modifyPublicStatus(Playlist playlist){
        playlist.modifyPlaylistPublicStatus();
        Playlist changedPlaylist = playlistRepository.save(playlist);
        return changedPlaylist.getIsPublic();
    }

    public Boolean checkUserPlaylistAuthority(User user, Playlist playlist){
        return playlist.getUser().equals(user);
    }

    public void deleteVideoInPlaylist(Video video, Long playlistId){
        Playlist playlist = findPlaylistEntity(playlistId);
        if(playlist==null)
            return;
        PlaylistVideo playlistVideo = this.findExistingPlaylistVideo(video, playlist);
        if(playlistVideo==null)
            return;
        playlistVideo.setStatus(false);
        pvRepository.save(playlistVideo);
    }


    public AllPlaylistResponseWithPageCount getAllPlaylistsResponse(Pageable pageable, String title, String video){
        Page<AllPlaylistsResponse> pageResponses = playlistRepository.findAllPlaylistsWithPageable(pageable, title, video);
        int totalPages = this.getTotalPageCount(pageResponses.getTotalElements());
        pageResponses.stream()
                .forEach(pr->pr.updateData(getFirstThumbnailInPlaylist(pr.getId()),findAllVideosInPlaylist(pr.getId()).size()));
        return new AllPlaylistResponseWithPageCount(totalPages, pageResponses.getContent());
    }

    private String getFirstThumbnailInPlaylist(Long playlistId){
        Playlist playlist = this.findPlaylistEntity(playlistId);
        return pvRepository.findFirstThumbnailUrl(playlist);
    }

    private int getTotalPageCount(long totalPlaylistsCount){
        if(totalPlaylistsCount<=8)
            return 1;
        return (int) (1+Math.ceil((totalPlaylistsCount-8)/(double)12));
    }

    private List<PlaylistVideo> findAllVideosInPlaylist(Long playlistId){
        Playlist playlist = this.findPlaylistEntity(playlistId);
        return playlist.getVideos()
                .stream()
                .filter(PlaylistVideo::getStatus)
                .sorted(Comparator.comparing(PlaylistVideo::getVideoOrder))
                .collect(Collectors.toList());
    }

    public List<AllPlaylistsResponse> getBestPlaylistsResponse(){
        List<AllPlaylistsResponse> bestPlaylists = playlistRepository.findBestPlaylists();
        bestPlaylists.forEach(r->r.updateData(getFirstThumbnailInPlaylist(r.getId()),findAllVideosInPlaylist(r.getId()).size()));
        return bestPlaylists;
    }

}
