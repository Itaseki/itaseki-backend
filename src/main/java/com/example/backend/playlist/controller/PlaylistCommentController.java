package com.example.backend.playlist.controller;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.playlist.dto.PlaylistCommentDto;
import com.example.backend.playlist.service.PlaylistCommentService;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.report.Report;
import com.example.backend.report.ReportService;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.VideoComment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        //principal 추가
        Long loginId=1L;
        User user=userService.findUserById(loginId);
        Playlist playlist=playlistService.findPlaylistEntity(playlistId);
        if(playlist==null){
            return new ResponseEntity<>("존재하지 않는 플레이리스트에 대한 댓글 등록 요청", HttpStatus.NOT_FOUND);
        }

        PlaylistComment playlistComment = PlaylistComment.builder()
                .content(commentDto.getContent()).user(user)
                .playlist(playlist).parentId(commentDto.getParentCommentId())
                .build();
        commentService.savePlaylistComment(playlistComment, commentDto.getParentCommentId());
        return new ResponseEntity<>("플레이리스트 댓글 등록 성공",HttpStatus.CREATED);
    }

    @DeleteMapping("/{playlistId}/comments/{playlistCommentId}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long playlistCommentId){
        PlaylistComment playlistComment = commentService.findPlaylistCommentById(playlistCommentId);
        commentService.deletePlaylistComment(playlistComment);
        return new ResponseEntity<>("플레이리스트 댓글 삭제 성공",HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{playlistId}/comments/{playlistCommentId}/reports")
    public ResponseEntity<String> reportVideoComment(@PathVariable Long playlistCommentId){
        Long loginId=1L;
        PlaylistComment comment = commentService.findPlaylistCommentById(playlistCommentId);
        User user = userService.findUserById(loginId);
        Boolean existence = reportService.checkReportExistence(user, comment);
        if(existence)
            return new ResponseEntity<>("해당 사용자가 이미 신고한 영상 댓글",HttpStatus.OK);
        Report report = Report.builder()
                .user(user).playlistComment(comment).build();
        reportService.saveReport(report);
        if(comment.getReports().size()>=5){
            commentService.deletePlaylistComment(comment);
            return new ResponseEntity<>("신고 5번 누적으로 삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("플레이리스트 댓글 신고 성공",HttpStatus.OK);
    }


}
