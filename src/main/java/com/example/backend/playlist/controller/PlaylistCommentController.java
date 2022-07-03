package com.example.backend.playlist.controller;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.playlist.dto.PlaylistCommentDto;
import com.example.backend.playlist.service.PlaylistCommentService;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
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

}
