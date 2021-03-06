package com.example.backend.playlist.service;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.playlist.domain.PlaylistVideo;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.playlist.dto.*;
import com.example.backend.playlist.repository.PlaylistRepository;
import com.example.backend.playlist.repository.PlaylistVideoRepository;
import com.example.backend.playlist.repository.UserSavedPlaylistRepository;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.Subscribe;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.dto.PlaylistVideoResponse;
import com.example.backend.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository pvRepository;
    private final VideoRepository videoRepository;
    private final UserSavedPlaylistRepository savedPlaylistRepository;
    private final PlaylistCommentService commentService;
    private final UserService userService;

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

    public List<Playlist> findAllPlaylistByUser(User user){
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
        savedPlaylistRepository.save(userSavedPlaylist); //saveCount ??? ??????
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


    public AllPlaylistResponseWithPageCount getAllPlaylistsResponse(Pageable pageable, String q, String nickname){
        List<String> queries=null;
        if(q!=null){
            queries=Arrays.stream(q.split(" ")).collect(Collectors.toList());
        }
        TempPlaylistDto fetchResult = playlistRepository.findAllPlaylistsWithPageable(pageable, queries, nickname);
        int totalPages = getTotalPageCount(fetchResult.getTotalCount());
        List<AllPlaylistsResponse> responses = fetchResult.getPlaylists();

        if(totalPages<=1)
            totalPages=1;

        responses.forEach(pr->pr.updateData(getFirstThumbnailInPlaylist(pr.getId()),findAllVideosInPlaylist(pr.getId()).size()));
        return new AllPlaylistResponseWithPageCount(totalPages, responses);
    }

    public List<AllPlaylistsResponse> getAllPlaylistForSearch(String q, String sort, String nickname){
        List<String> queries=null;
        if(q!=null){
            queries=Arrays.stream(q.split(" ")).collect(Collectors.toList());
        }
        List<AllPlaylistsResponse> search = playlistRepository.findAllForSearch(sort, nickname, queries);
        search.forEach(pr->pr.updateData(getFirstThumbnailInPlaylist(pr.getId()),findAllVideosInPlaylist(pr.getId()).size()));
        return search;
    }

    private String getFirstThumbnailInPlaylist(Long playlistId){
        Playlist playlist = this.findPlaylistEntity(playlistId);
        return pvRepository.findFirstThumbnailUrl(playlist);
    }

    private int getTotalPageCount(long totalPlaylistsCount){
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

    public DetailPlaylistResponse getDetailVideoResponse(Playlist playlist, Long loginId){
        playlist.updateViewCount();
        playlistRepository.save(playlist);
        User playlistWriter = playlist.getUser();
        Long playlistWriterId=playlistWriter.getUserId();
        List<PlaylistComment> parentComments = playlist.getComments()
                .stream()
                .filter(comment -> comment.getIsParentComment().equals(true)) //??????????????? ??????
                .collect(Collectors.toList());
        List<PlaylistCommentsResponse> playlistCommentResponses = commentService.getPlaylistCommentResponses(parentComments, loginId, playlistWriterId);
        List<PlaylistVideoResponse> videos = this.findAllVideosInPlaylist(playlist.getId())
                .stream()
                .map(pv-> PlaylistVideoResponse.fromEntity(pv.getVideo()))
                .collect(Collectors.toList());
        return DetailPlaylistResponse.fromEntity(playlist,playlistCommentResponses,videos,loginId);

    }

    public List<Playlist> findAllPublicPlaylistsByUserDesc(User user){
        return this.findAllPlaylistByUser(user)
                .stream()
                .filter(Playlist::getIsPublic)
                .sorted(Comparator.comparing(Playlist::getId).reversed())
                .collect(Collectors.toList());
    }

    public SubscribePlaylistResponseWithPageCount getSubscribingPlaylists(User user, int pageNumber, String sort){
        int userCount=4;
        int offset=userCount*pageNumber;

        List<SubscribePlaylistResponse> responses=new ArrayList<>();
        List<Subscribe> targets = userService.findAllSubscribingTargets(user);
        List<User> subscribers=sort.contains("id")?this.getSubscribedPlaylistsByTime(targets):this.getSubscribeUserByLikeCount(targets);
//        List<User> subscribers = this.getSubscribedPlaylistsByTime(targets);
        List<List<Playlist>> collect = subscribers.stream()
                .map(this::findAllPublicPlaylistsByUserDesc)
                .collect(Collectors.toList());


        for(int i=0;i<collect.size();i++){
            List<Playlist> p=collect.get(i); //p=??? ???????????? ??? ??????
            if(p.isEmpty())
                continue;
            User writer = subscribers.get(i);
            List<AllPlaylistsResponse> res = p.stream().map(AllPlaylistsResponse::new).collect(Collectors.toList());
            res.forEach(r->r.updateData(getFirstThumbnailInPlaylist(r.getId()),findAllVideosInPlaylist(r.getId()).size()));
            responses.add(new SubscribePlaylistResponse(writer.getNickname(),res));
        }

        int pageCount= (int) Math.ceil(responses.size()/(double)4);
        System.out.println("pageCount = " + pageCount);

        List<SubscribePlaylistResponse> collect1 = responses.stream().skip(offset).limit(userCount).collect(Collectors.toList());
        return new SubscribePlaylistResponseWithPageCount(pageCount,collect1);

    }

    private List<User> getSubscribeUserByLikeCount(List<Subscribe> targets){
        return targets.stream()
                .sorted((p1, p2) -> this.totalLikeCountOnUserPlaylists(p2.getUser()).compareTo(this.totalLikeCountOnUserPlaylists(p1.getUser())))
                .map(Subscribe::getSubscribeTarget)
                .collect(Collectors.toList());

    }

    private Integer totalLikeCountOnUserPlaylists(User user){
        return this.findAllPlaylistByUser(user)
                .stream()
                .mapToInt(Playlist::getLikeCount).sum();
    }

    private List<User> getSubscribedPlaylistsByTime(List<Subscribe> targets){
        return targets.stream()
                .sorted(Comparator.comparing(Subscribe::getLastModified).reversed())
                .map(Subscribe::getSubscribeTarget)
                .collect(Collectors.toList());

    }

    public void deletePlaylist(Playlist playlist){
        playlist.setStatus(false);
        playlistRepository.save(playlist);
    }

}
