package com.example.backend.playlist;

import com.example.backend.playlist.dto.MyPlaylistResponse;
import com.example.backend.playlist.dto.NewEmptyPlaylistDto;
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

}
