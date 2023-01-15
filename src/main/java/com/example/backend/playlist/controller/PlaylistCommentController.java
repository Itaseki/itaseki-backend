package com.example.backend.playlist.controller;

import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.playlist.dto.PlaylistCommentDto;
import com.example.backend.playlist.service.PlaylistCommentService;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.report.Report;
import com.example.backend.report.ReportService;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards/playlist")
@RequiredArgsConstructor
public class PlaylistCommentController {
    private final PlaylistService playlistService;
    private final UserService userService;
    private final PlaylistCommentService commentService;
    private final ReportService reportService;

    @PostMapping("/{playlistId}/comments")
    public ResponseEntity<String> addCommentToPlaylist(@PathVariable Long playlistId, @RequestBody PlaylistCommentDto commentDto){
        PlaylistComment playlistComment = PlaylistComment.builder()
                .content(commentDto.getContent()).user(findUserByAuthentication())
                .playlist(playlistService.findPlaylistEntity(playlistId)).parentId(commentDto.getParentCommentId())
                .build();
        commentService.savePlaylistComment(playlistComment, commentDto.getParentCommentId());
        return new ResponseEntity<>("플레이리스트 댓글 등록 성공",HttpStatus.CREATED);
    }

    @DeleteMapping("/{playlistId}/comments/{playlistCommentId}")
    public ResponseEntity<String> deletePlaylistComment(@PathVariable Long playlistCommentId){
        PlaylistComment playlistComment = commentService.findPlaylistCommentById(playlistCommentId);
        findUserAndCheckAuthority(playlistComment.getUser().getUserId());
        commentService.deletePlaylistComment(playlistComment);
        return new ResponseEntity<>("플레이리스트 댓글 삭제 성공",HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{playlistId}/comments/{playlistCommentId}/reports")
    public ResponseEntity<String> reportPlaylistComment(@PathVariable Long playlistCommentId){
        PlaylistComment comment = commentService.findPlaylistCommentById(playlistCommentId);
        User user = findUserByAuthentication();
        Boolean existence = reportService.checkReportExistence(user, comment);
        if(existence) {
            return new ResponseEntity<>("해당 사용자가 이미 신고한 영상 댓글", HttpStatus.OK);
        }
        Report report = Report.builder()
                .user(user).playlistComment(comment).build();
        reportService.saveReport(report, comment.getUser());
        if(comment.getReports().size() >= 5){
            commentService.deletePlaylistComment(comment);
            return new ResponseEntity<>("신고 5번 누적으로 삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("플레이리스트 댓글 신고 성공",HttpStatus.OK);
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
}
