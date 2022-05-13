package com.example.backend.video;

import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import com.example.backend.video.dto.DetailVideoResponse;
import com.example.backend.video.dto.VideoCommentDto;
import com.example.backend.video.dto.VideoDto;
import com.example.backend.video.dto.VideoUploadInfoResponse;
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
    private final VideoCommentService commentService;

    @GetMapping("")
    public ResponseEntity<String> verifyVideoUrl(@RequestParam String url){
        String existence= videoService.checkVideoUrlExistence(url);
        return new ResponseEntity<>(existence,HttpStatus.OK);
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<VideoUploadInfoResponse> getInfoForVideoUpload(@PathVariable Long userId){
        User user = userService.findUserById(userId);
        return new ResponseEntity<>(videoService.getPreInfoForVideoUpload(user),HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> uploadVideo(@RequestBody VideoDto videoDto){
        Long loginId=1L;
        User user = userService.findUserById(loginId);
        videoService.saveVideo(videoDto,user);
        return new ResponseEntity<>("영상 등록 성공", HttpStatus.CREATED);
    }

    @PostMapping("/{videoId}/comments")
    public ResponseEntity<String> uploadVideoComment(@PathVariable Long videoId, @RequestBody VideoCommentDto commentDto){
        //principal 추가
        Long loginId=1L;
        User user=userService.findUserById(loginId);
        Video video = videoService.findVideoEntityById(videoId);
        if(video==null){
            return new ResponseEntity<>("존재하지 않는 영상에 대한 댓글 등록 요청",HttpStatus.NOT_FOUND);
        }
        VideoComment videoComment = VideoComment.builder()
                .content(commentDto.getContent()).user(user)
                .video(video).parentId(commentDto.getParentCommentId())
                .build();
        commentService.saveVideoComment(videoComment, commentDto.getParentCommentId());
        return new ResponseEntity<>("영상 댓글 등록 성공",HttpStatus.CREATED);

    }

    @GetMapping("/{videoId}")
    public ResponseEntity<DetailVideoResponse> getDetailVideo(@PathVariable Long videoId){
        Long loginId=1L;
        Video video = videoService.findVideoEntityById(videoId);
        if(video==null)
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        DetailVideoResponse detailVideoResponse = videoService.getDetailVideoResponse(video, loginId);
        videoService.updateVideoViewCount(video);
        return new ResponseEntity<>(detailVideoResponse,HttpStatus.OK);
    }



}
