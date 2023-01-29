package com.example.backend.video;

import com.example.backend.globalexception.ExceptionHeader;
import com.example.backend.like.Like;
import com.example.backend.like.LikeService;
import com.example.backend.report.Report;
import com.example.backend.report.ReportService;
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
    private final LikeService likeService;
    private final ReportService reportService;
    private final ExceptionHeader exceptionHeader;

    @GetMapping(value = "/verify", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> verifyVideoUrl(@RequestParam String url) {
        return new ResponseEntity<>(videoService.checkVideoUrlExistence(url), HttpStatus.OK);
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<VideoUploadInfoResponse> getInfoForVideoUpload(@PathVariable Long userId) {
        return new ResponseEntity<>(videoService.getPreInfoForVideoUpload(findUserAndCheckAuthority(userId)),HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> uploadVideo(@RequestBody VideoDto videoDto) {
        videoService.saveVideo(videoDto, findUserByAuthentication());
        return new ResponseEntity<>("영상 등록 성공", HttpStatus.CREATED);
    }

    @PostMapping("/{videoId}/comments")
    public ResponseEntity<String> uploadVideoComment(@PathVariable Long videoId, @RequestBody VideoCommentDto commentDto){
        Video video = videoService.findVideoEntityById(videoId);
        VideoComment videoComment = VideoComment.builder()
                .content(commentDto.getContent()).user(findUserByAuthentication())
                .video(video).parentId(commentDto.getParentCommentId())
                .build();
        commentService.saveVideoComment(videoComment, commentDto.getParentCommentId());
        return new ResponseEntity<>("영상 댓글 등록 성공",HttpStatus.CREATED);

    }

    @GetMapping("/{videoId}")
    public ResponseEntity<DetailVideoResponse> getDetailVideo(@PathVariable Long videoId){
        Video video = videoService.findVideoEntityById(videoId);
        DetailVideoResponse detailVideoResponse = videoService.getDetailVideoResponse(video,
                findUserOrAnonymousUser().getUserId());
        videoService.updateVideoViewCount(video);
        return new ResponseEntity<>(detailVideoResponse,HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<AllVideoResponseWithPageCount> getAllVideos(@PageableDefault(size=12, sort="id",direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(videoService.getAllVideosResponse(pageable),HttpStatus.OK);
    }

    @GetMapping("/best")
    public ResponseEntity<List<AllVideoResponse>> getBestVideos(){
        return new ResponseEntity<>(videoService.getBestVideos(),HttpStatus.OK);
    }

    @PostMapping("/{videoId}/likes")
    public ResponseEntity<Integer> likeOnVideo(@PathVariable Long videoId){
        Video video = videoService.findVideoEntityById(videoId);
        User user = findUserByAuthentication();
        Like like = likeService.findExistingLike(video, user);
        Integer likeCount;
        if(like == null){
            like=Like.builder()
                    .video(video).user(user).build();
            likeCount = video.updateLikeCount(1);
        } else {
            Boolean likeStatus = like.modifyLikeStatus();
            likeCount = video.updateLikeCount(likeStatus?1:-1);
        }
        likeService.saveLike(like);
        return new ResponseEntity<>(likeCount,HttpStatus.OK);
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long videoId) {
        Video video = videoService.findVideoEntityById(videoId);
        findUserAndCheckAuthority(video.getUser().getUserId());
        videoService.deleteVideo(video);
        return new ResponseEntity<>("영상 삭제 성공",HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{videoId}/reports")
    public ResponseEntity<String> reportVideo(@PathVariable Long videoId) {
        Video video = videoService.findVideoEntityById(videoId);
        User user = findUserByAuthentication();
        if(reportService.checkReportExistence(user, video)) {
            return new ResponseEntity<>("해당 사용자가 이미 신고한 영상", HttpStatus.OK);
        }
        Report report = Report.builder()
                .user(user).video(video).build();
        reportService.saveReport(report, video.getUser());
        if(video.getReports().size() >= 5) {
            videoService.deleteVideo(video);
            return new ResponseEntity<>("신고 5번 누적으로 삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("영상 신고 성공",HttpStatus.OK);
    }

    @DeleteMapping("/{videoId}/comments/{videoCommentId}")
    public ResponseEntity<String> deleteVideoComment(@PathVariable Long videoId, @PathVariable Long videoCommentId){
        VideoComment videoComment = commentService.findVideoCommentById(videoCommentId);
        findUserAndCheckAuthority(videoComment.getUser().getUserId());
        commentService.deleteVideoComment(videoComment);
        return new ResponseEntity<>("영상 댓글 삭제 성공",HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{videoId}/comments/{videoCommentId}/reports")
    public ResponseEntity<String> reportVideoComment(@PathVariable Long videoId, @PathVariable Long videoCommentId){
        VideoComment comment = commentService.findVideoCommentById(videoCommentId);
        User user = findUserByAuthentication();
        Boolean existence = reportService.checkReportExistence(user, comment);
        if(existence) {
            return new ResponseEntity<>("해당 사용자가 이미 신고한 영상 댓글", HttpStatus.OK);
        }
        Report report = Report.builder()
                .user(user).videoComment(comment).build();
        reportService.saveReport(report, comment.getUser());
        if(comment.getReports().size() >= 5){
            commentService.deleteVideoComment(comment);
            return new ResponseEntity<>("신고 5번 누적으로 삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("영상 댓글 신고 성공",HttpStatus.OK);
    }

    @GetMapping("/series/search")
    public ResponseEntity<List<InnerInfoResponse>> searchSeriesName(@RequestParam String q) {
        return new ResponseEntity<>(videoService.findSeriesNameByQuery(q),HttpStatus.OK);
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
