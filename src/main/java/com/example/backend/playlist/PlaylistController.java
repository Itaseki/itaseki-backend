package com.example.backend.playlist;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.UserSavedPlaylist;
import com.example.backend.playlist.dto.*;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{userId}")
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
        playlistService.userPlaylistSave(playlist,user);
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

}
