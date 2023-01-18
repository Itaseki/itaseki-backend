package com.example.backend.playlist.controller;

import com.example.backend.like.Like;
import com.example.backend.like.LikeService;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.playlist.dto.*;
import com.example.backend.report.Report;
import com.example.backend.report.ReportService;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<MyPlaylistResponse> createNewPlaylist(@RequestBody NewEmptyPlaylistDto playlistDto) {
        return new ResponseEntity<>(playlistService.saveEmptyPlaylist(playlistDto,findUserByAuthentication()), HttpStatus.CREATED);
    }

    @PostMapping("/{playlistId}")
    public ResponseEntity<String> addVideoToPlaylist(@PathVariable Long playlistId, @RequestBody AddVideoDto dto){
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        findUserAndCheckAuthority(playlist.getUser().getUserId());
        if(playlistService.checkVideoPlaylistExistence(dto.getVideoId(),playlist)) {
            return new ResponseEntity<>("이미 해당 플레이리스트에 추가된 영상", HttpStatus.CONFLICT);
        }
        playlistService.addVideoToPlaylist(dto.getVideoId(),playlist);
        return new ResponseEntity<>("영상 추가 성공",HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MyPlaylistResponse>> getMyPlaylists(@PathVariable Long userId){
        return new ResponseEntity<>(playlistService.getMyPlaylist(findUserAndCheckAuthority(userId)),HttpStatus.OK);
    }

    @PostMapping("/saved")
    public ResponseEntity<String> saveOtherUserPlaylist(@RequestBody AddPlaylistDto dto){
        User user = findUserByAuthentication();
        Playlist playlist = playlistService.findPlaylistEntity(dto.getPlaylistId());
        UserSavedPlaylist savedPlaylist = playlistService.findExistingSavedPlaylist(user, playlist);
        if(savedPlaylist != null) {
            return new ResponseEntity<>("이미 저장된 플레이리스트", HttpStatus.CONFLICT);
        }
        playlistService.saveOthersPlaylist(playlist, user);
        return new ResponseEntity<>("플레이리스트 저장 성공",HttpStatus.CREATED);
    }

    @GetMapping("/saved")
    public ResponseEntity<List<PlaylistTitleResponse>> getSavedPlaylists(){
        return new ResponseEntity<>(playlistService.getUserSavedPlaylists(findUserByAuthentication()), HttpStatus.OK);
    }

    @PatchMapping("/{playlistId}")
    public ResponseEntity<String> changePlaylistPublic(@PathVariable Long playlistId){
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        findUserAndCheckAuthority(playlist.getUser().getUserId());
        Boolean changedStatus = playlistService.modifyPublicStatus(playlist);
        return new ResponseEntity<>(changedStatus?"공개":"비공개",HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<AllPlaylistResponseWithPageCount> getAllPlaylists(@PageableDefault(size=12, sort="id",direction = Sort.Direction.DESC) Pageable pageable,
                                                                            @RequestParam(required = false) String q, @RequestParam(required = false) String nickname) {
        return new ResponseEntity<>(playlistService.getAllPlaylistsResponse(pageable, q, nickname),HttpStatus.OK);
    }

    @GetMapping("/best")
    public ResponseEntity<List<AllPlaylistsResponse>> getBestPlaylists() {
        return new ResponseEntity<>(playlistService.getBestPlaylistsResponse(),HttpStatus.OK);
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<DetailPlaylistResponse> getDetailPlaylist(@PathVariable Long playlistId) {
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        return new ResponseEntity<>(playlistService.getDetailVideoResponse(playlist,
                findUserByAuthentication().getUserId()),HttpStatus.OK);
    }

    @GetMapping("/subscribe")
    public ResponseEntity<SubscribePlaylistResponseWithPageCount> getSubscribePlaylists(@RequestParam int page, @RequestParam String sort){
        return new ResponseEntity<>(playlistService.getSubscribingPlaylists(findUserByAuthentication(),page, sort),HttpStatus.OK);
    }

    @PostMapping("/{playlistId}/likes")
    public ResponseEntity<Integer> likeOnPlaylist(@PathVariable Long playlistId) {
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        User user = findUserByAuthentication();
        Like like = likeService.findExistingLike(playlist, user);
        Integer likeCount;
        if(like == null) {
            like=Like.builder()
                    .playlist(playlist).user(user).build();
            likeCount = playlist.updateLikeCount(1);
        } else {
            Boolean likeStatus = like.modifyLikeStatus();
            likeCount = playlist.updateLikeCount(likeStatus?1:-1);
        }
        likeService.saveLike(like);
        return new ResponseEntity<>(likeCount,HttpStatus.OK);
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long playlistId){
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        findUserAndCheckAuthority(playlist.getUser().getUserId());
        playlistService.deletePlaylist(playlist);
        return new ResponseEntity<>("플레이리스트 삭제 성공",HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{playlistId}/reports")
    public ResponseEntity<String> reportVideo(@PathVariable Long playlistId){
        Playlist playlist = playlistService.findPlaylistEntity(playlistId);
        User user = findUserByAuthentication();
        Boolean existence = reportService.checkReportExistence(user, playlist);
        if(existence) {
            return new ResponseEntity<>("해당 사용자가 이미 신고한 플레이리스트", HttpStatus.OK);
        }
        Report report = Report.builder()
                .user(user).playlist(playlist).build();
        reportService.saveReport(report, playlist.getUser());
        if(playlist.getReports().size() >= 5){
            playlistService.deletePlaylist(playlist);
            return new ResponseEntity<>("신고 5번 누적으로 삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("플레이리스트 신고 성공",HttpStatus.OK);
    }

    private User findUserByAuthentication() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser")) {
            return User.createAnonymousUser();
        }
        UserDetails user = (UserDetails) principal;
        return userService.findUserById(Long.parseLong(user.getUsername()));
    }

    private User findUserAndCheckAuthority(Long userId) {
        User user = userService.findExistingUser(userId);
        userService.checkUserAuthority(findUserByAuthentication().getUserId(), userId);
        return user;
    }

}
