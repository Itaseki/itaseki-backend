package com.example.backend.main;

import com.example.backend.main.dto.MainCommunityResponse;
import com.example.backend.main.dto.MainImageResponse;
import com.example.backend.main.dto.MainUserResponse;
import com.example.backend.main.dto.MainVideoResponse;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;

    @GetMapping("/community")
    public ResponseEntity<List<MainCommunityResponse>> getMainCommunityBoards(){
        return new ResponseEntity<>(mainService.getCommunityForMain(), HttpStatus.OK);
    }

    @GetMapping("/image")
    public ResponseEntity<List<MainImageResponse>> getMainImageBoards(){
        return new ResponseEntity<>(mainService.getImageForMain(),HttpStatus.OK);
    }

    @GetMapping("/video")
    public ResponseEntity<List<MainVideoResponse>> getMainVideoBoards(){
        return new ResponseEntity<>(mainService.getVideoForMain(),HttpStatus.OK);
    }

    @GetMapping("/playlist")
    public ResponseEntity<List<AllPlaylistsResponse>> getMainPlaylistBoards(){
        return new ResponseEntity<>(mainService.getPlaylistsForMain(),HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<MainUserResponse> getMainUserInfo(){
        Long loginId=1L;
        return new ResponseEntity<>(mainService.getUserProfileForMain(loginId),HttpStatus.OK);
    }



}
