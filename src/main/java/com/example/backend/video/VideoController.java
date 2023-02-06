package com.example.backend.video;

import com.example.backend.globalexception.ExceptionHeader;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import com.example.backend.video.dto.*;
import com.example.backend.video.exception.WrongVideoUrlException;
import com.example.backend.video.service.VideoCommentService;
import com.example.backend.video.service.VideoService;
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
@RequestMapping("/boards/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    private final UserService userService;
    private final VideoCommentService commentService;
    private final ExceptionHeader exceptionHeader;

    @GetMapping(value = "/verify", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> verifyVideoUrl(@RequestParam String url) {
        return new ResponseEntity<>(videoService.checkVideoUrlExistence(url), HttpStatus.OK);
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<VideoUploadInfoResponse> getInfoForVideoUpload(@PathVariable Long userId) {
        return new ResponseEntity<>(videoService.getPreInfoForVideoUpload(findUserAndCheckAuthority(userId)),
                HttpStatus.OK);
    }

    @PostMapping(value = "", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> uploadVideo(@RequestBody VideoPostRequest videoPostRequest) {
        videoService.saveVideo(videoPostRequest, findUserByAuthentication());
        return new ResponseEntity<>("영상 등록 성공", HttpStatus.CREATED);
    }

    @PostMapping(value = "/{videoId}/comments", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> uploadVideoComment(@PathVariable Long videoId,
                                                     @RequestBody VideoCommentRequest request) {
        commentService.saveVideoComment(request.toEntity(findUserByAuthentication(),
                videoService.findVideoEntityById(videoId),
                commentService.findParentComment(request.getParentCommentId(),
                        videoService.findVideoEntityById(videoId))));
        return new ResponseEntity<>("영상 댓글 등록 성공", HttpStatus.CREATED);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<DetailVideoResponse> getDetailVideo(@PathVariable Long videoId) {
        return new ResponseEntity<>(videoService.getDetailVideoResponse(videoService.findVideoEntityById(videoId),
                findUserOrAnonymousUser().getUserId()), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<AllVideoResponseWithPageCount> getAllVideos(
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(videoService.getAllVideosResponse(pageable), HttpStatus.OK);
    }

    @GetMapping("/best")
    public ResponseEntity<List<AllVideoResponse>> getBestVideos() {
        return new ResponseEntity<>(videoService.getBestVideos(), HttpStatus.OK);
    }

    @PostMapping("/{videoId}/likes")
    public ResponseEntity<Integer> likeOnVideo(@PathVariable Long videoId) {
        return new ResponseEntity<>(videoService.updateLikeOnVideo(videoId, findUserByAuthentication()), HttpStatus.OK);
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long videoId) {
        Video video = videoService.findVideoEntityById(videoId);
        findUserAndCheckAuthority(video.getUser().getUserId());
        videoService.deleteVideo(video);
        return new ResponseEntity<>("영상 삭제 성공", HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{videoId}/reports", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> reportVideo(@PathVariable Long videoId) {
        Video video = videoService.findVideoEntityById(videoId);
        if (videoService.isVideoAlreadyReported(video, findUserByAuthentication())) {
            return new ResponseEntity<>("해당 사용자가 이미 신고한 영상", HttpStatus.OK);
        }
        if (videoService.isVideoDeletedByReport(video)) {
            return new ResponseEntity<>("신고 5번 누적으로 삭제", HttpStatus.OK);
        }
        return new ResponseEntity<>("영상 신고 성공", HttpStatus.OK);
    }

    @DeleteMapping("/{videoId}/comments/{videoCommentId}")
    public ResponseEntity<String> deleteVideoComment(@PathVariable Long videoId, @PathVariable Long videoCommentId) {
        VideoComment videoComment = commentService.findVideoCommentById(videoCommentId,
                videoService.findVideoEntityById(videoId));
        findUserAndCheckAuthority(videoComment.getUser().getUserId());
        commentService.deleteVideoComment(videoComment);
        return new ResponseEntity<>("영상 댓글 삭제 성공", HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{videoId}/comments/{videoCommentId}/reports", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> reportVideoComment(@PathVariable Long videoId, @PathVariable Long videoCommentId) {
        VideoComment comment = commentService.findVideoCommentById(videoCommentId,
                videoService.findVideoEntityById(videoId));
        if (commentService.isCommentAlreadyReported(comment, findUserByAuthentication())) {
            return new ResponseEntity<>("해당 사용자가 이미 신고한 영상 댓글", HttpStatus.OK);
        }
        if (commentService.isCommentDeletedByReport(comment)) {
            return new ResponseEntity<>("신고 5번 누적으로 삭제", HttpStatus.OK);
        }
        return new ResponseEntity<>("영상 댓글 신고 성공", HttpStatus.OK);
    }

    @GetMapping("/series/search")
    public ResponseEntity<List<NameIdResponse>> searchSeriesName(@RequestParam String q) {
        return new ResponseEntity<>(videoService.findSeriesNameByQuery(q), HttpStatus.OK);
    }

    private User findUserByAuthentication() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findUserById(Long.parseLong(principal.getUsername()));
    }

    private User findUserAndCheckAuthority(Long userId) {
        User user = userService.findExistingUser(userId);
        userService.checkUserAuthority(findUserByAuthentication().getUserId(), userId);
        return user;
    }

    private User findUserOrAnonymousUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser")) {
            return User.createAnonymousUser();
        }
        UserDetails user = (UserDetails) principal;
        return userService.findUserById(Long.parseLong(user.getUsername()));
    }

    @ExceptionHandler(WrongVideoUrlException.class)
    public ResponseEntity<String> handleWrongValueException(WrongVideoUrlException exception) {
        return new ResponseEntity<>(exception.getMessage(), exceptionHeader.header, HttpStatus.BAD_REQUEST);
    }
}
