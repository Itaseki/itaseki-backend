package com.example.backend.video;

import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.video.dto.VideoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<String> verifyVideoUrl(@RequestParam String url){
        String existence= videoService.checkVideoUrlExistence(url);
        return new ResponseEntity<>(existence,HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> uploadVideo(@RequestBody VideoDto videoDto){
        Long loginId=1L;
        User user = userService.findUserById(loginId);
        videoService.saveVideo(videoDto,user);
        return new ResponseEntity<>("영상 등록 성공", HttpStatus.CREATED);
    }

}
