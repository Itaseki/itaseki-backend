package com.example.backend.playlist.controller;

import com.example.backend.like.Like;
import com.example.backend.like.LikeService;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.playlist.dto.*;
import com.example.backend.report.Report;
import com.example.backend.report.ReportService;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;
    private final UserService userService;
    private final LikeService likeService;
    private final ReportService reportService;

    @PostMapping("")
    public ResponseEntity<MyPlaylistResponse> createNewPlaylist(@RequestBody NewEmptyPlaylistDto playlistDto){
        Long loginId=1L;
        User user = userService.findUserById(loginId);
        return new ResponseEntity<>(playlistService.saveEmptyPlaylist(playlistDto,user), HttpStatus.CREATED);
    }

    @PostMapping("/{playlistId}")
    public ResponseEntity<String> addVideoToPlaylist(@PathVariable Long playlistId, @RequestBody AddVideoDto dto){
        Long loginId=1L;
        User user = userService.findUserById(loginId);
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        if(!playlistService.checkUserPlaylistAuthority(user,playlist))
            return new ResponseEntity<>("권한 없음",HttpStatus.FORBIDDEN);
        if(playlistService.checkVideoPlaylistExistence(dto.getVideoId(),playlist))
            return new ResponseEntity<>("이미 해당 플레이리스트에 추가된 영상",HttpStatus.CONFLICT);
        playlistService.addVideoToPlaylist(dto.getVideoId(),playlist);
        return new ResponseEntity<>("영상 추가 성공",HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MyPlaylistResponse>> getMyPlaylists(@PathVariable Long userId){
        User user = userService.findUserById(userId);
        return new ResponseEntity<>(playlistService.getMyPlaylist(user),HttpStatus.OK);
    }

    @PostMapping("/saved")
    public ResponseEntity<String> saveOtherUserPlaylist(@RequestBody AddPlaylistDto dto){
        Long loginId=1L;
        User user = userService.findUserById(loginId);
        Playlist playlist = playlistService.findPlaylistEntity(dto.getPlaylistId());
        UserSavedPlaylist savedPlaylist = playlistService.findExistingSavedPlaylist(user, playlist);
        if(savedPlaylist!=null)
            return new ResponseEntity<>("이미 저장된 플레이리스트",HttpStatus.CONFLICT);
        playlistService.saveOthersPlaylist(playlist,user);
        return new ResponseEntity<>("플레이리스트 저장 성공",HttpStatus.CREATED);
    }

    @GetMapping("/saved")
    public ResponseEntity<List<PlaylistTitleResponse>> getSavedPlaylists(){
        Long loginId=1L;
        User user = userService.findUserById(loginId);
        return new ResponseEntity<>(playlistService.getUserSavedPlaylists(user),HttpStatus.OK);
    }

    @PatchMapping("/{playlistId}")
    public ResponseEntity<String> changePlaylistPublic(@PathVariable Long playlistId){
        Long loginId=1L;
        User user = userService.findUserById(loginId);
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        if(!playlistService.checkUserPlaylistAuthority(user,playlist))
            return new ResponseEntity<>("권한 없음",HttpStatus.FORBIDDEN);
        Boolean changedStatus = playlistService.modifyPublicStatus(playlist);
        return new ResponseEntity<>(changedStatus?"공개":"비공개",HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<AllPlaylistResponseWithPageCount> getAllPlaylists(@PageableDefault(size=12, sort="id",direction = Sort.Direction.DESC) Pageable pageable,
                                                                            @RequestParam(required = false) String q, @RequestParam(required = false) String nickname){
        return new ResponseEntity<>(playlistService.getAllPlaylistsResponse(pageable, q, nickname),HttpStatus.OK);
    }

    @GetMapping("/best")
    public ResponseEntity<List<AllPlaylistsResponse>> getBestPlaylists(){
        return new ResponseEntity<>(playlistService.getBestPlaylistsResponse(),HttpStatus.OK);
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<DetailPlaylistResponse> getDetailPlaylist(@PathVariable Long playlistId){
        Long loginId=1L;
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        return new ResponseEntity<>(playlistService.getDetailVideoResponse(playlist,loginId),HttpStatus.OK);
    }

    @GetMapping("/subscribe")
    public ResponseEntity<SubscribePlaylistResponseWithPageCount> getSubscribePlaylists(@RequestParam int page, @RequestParam String sort){
        User user = userService.findUserById(3L);
        return new ResponseEntity<>(playlistService.getSubscribingPlaylists(user,page, sort),HttpStatus.OK);
    }

    @PostMapping("/{playlistId}/likes")
    public ResponseEntity<Integer> likeOnVideo(@PathVariable Long playlistId) {
        Long loginId = 1L;
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        User user = userService.findUserById(loginId);
        Like like = likeService.findExistingLike(playlist, user);
        Integer likeCount;
        if(like==null){
            like=Like.builder()
                    .playlist(playlist).user(user).build();
            likeCount = playlist.updateLikeCount(1);
        }else{
            Boolean likeStatus = like.modifyLikeStatus();
            likeCount = playlist.updateLikeCount(likeStatus?1:-1);
        }
        likeService.saveLike(like);
        return new ResponseEntity<>(likeCount,HttpStatus.OK);
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long playlistId){
        //권한 확인
        Long loginId=1L;
        User user = userService.findUserById(loginId);
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        if(!playlistService.checkUserPlaylistAuthority(user, playlist))
            return new ResponseEntity<>("권한 없음",HttpStatus.FORBIDDEN);
        playlistService.deletePlaylist(playlist);
        return new ResponseEntity<>("플레이리스트 삭제 성공",HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{playlistId}/reports")
    public ResponseEntity<String> reportVideo(@PathVariable Long playlistId){
        Long loginId=1L;
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        User user = userService.findUserById(loginId);
        Boolean existence = reportService.checkReportExistence(user, playlist);
        if(existence)
            return new ResponseEntity<>("해당 사용자가 이미 신고한 플레이리스트",HttpStatus.OK);
        Report report = Report.builder()
                .user(user).playlist(playlist).build();
        reportService.saveReport(report);
        if(playlist.getReports().size()>=5){
            playlistService.deletePlaylist(playlist);
            return new ResponseEntity<>("신고 5번 누적으로 삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("플레이리스트 신고 성공",HttpStatus.OK);
    }



//    @PostMapping("/subscribe/{userId}")
//    public ResponseEntity<String> subscribe(@PathVariable Long userId){
//        User user = userService.findUserById(3L);
//        User target = userService.findUserById(userId);
//        userService.saveSubscribe(user,target);
//        return new ResponseEntity<>("success",HttpStatus.OK);
//    }

}
