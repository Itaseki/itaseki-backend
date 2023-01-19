package com.example.backend.main;

import com.example.backend.main.dto.MainPlaylistResponse;
import com.example.backend.main.dto.MainUserResponse;
import com.example.backend.main.dto.MainVideo;
import com.example.backend.user.domain.User;
import com.example.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;
    private final UserService userService;

    @GetMapping("/video")
    public ResponseEntity<List<MainVideo>> getMainVideoBoards(){
        return new ResponseEntity<>(mainService.getVideoForMain(),HttpStatus.OK);
    }

    @GetMapping("/playlist")
    public ResponseEntity<MainPlaylistResponse> getMainPlaylistBoards(){
        return new ResponseEntity<>(mainService.getPlaylistsForMain(),HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<MainUserResponse> getMainUserInfo(){
        return new ResponseEntity<>(mainService.getUserProfileForMain(findUserByAuthentication().getUserId()),HttpStatus.OK);
    }

    private User findUserByAuthentication() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findUserById(Long.parseLong(principal.getUsername()));
    }
}
