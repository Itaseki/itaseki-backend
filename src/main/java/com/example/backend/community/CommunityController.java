package com.example.backend.community;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.dto.CommunityBoardDto;
import com.example.backend.community.dto.CommunityCommentDto;
import com.example.backend.community.dto.DetailCommunityBoardResponse;
import com.example.backend.community.service.CommunityBoardService;
import com.example.backend.community.service.CommunityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/boards/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityBoardService communityBoardService;
    private final CommunityCommentService commentService;

    @PostMapping("")
    public ResponseEntity<String> createCommunityPost(CommunityBoardDto communityBoardDto){
        CommunityBoard post= CommunityBoard.builder()
                .title(communityBoardDto.getTitle()).content(communityBoardDto.getContent()).createdTime(LocalDateTime.now())
                .build();
        communityBoardService.savePost(post,communityBoardDto.getFiles());
        return new ResponseEntity<>("잡담 게시글 등록 성공", HttpStatus.CREATED);
    }

    @PostMapping("/{communityBoardId}/comments")
    public ResponseEntity<String> createCommunityComment(@PathVariable Long communityBoardId, @RequestBody CommunityCommentDto commentDto){
        CommunityBoard targetBoard=communityBoardService.findCommunityBoardEntity(communityBoardId);
        if(targetBoard==null){
            return new ResponseEntity<>("존재하지 않는 게시글에 대한 댓글 등록 요청",HttpStatus.NOT_FOUND);
        }
        CommunityComment comment=CommunityComment.builder()
                .content(commentDto.getContent()).parentId(commentDto.getParentCommentId())
                .createdTime(LocalDateTime.now()).communityBoard(targetBoard)
                .build();
        commentService.saveCommunityComment(comment, commentDto.getParentCommentId());
        return new ResponseEntity<>("잡담 게시판 댓글 등록 성공",HttpStatus.CREATED);

    }

    @GetMapping("/{communityBoardId}")
    public ResponseEntity<DetailCommunityBoardResponse> getDetailCommunityBoard(@PathVariable Long communityBoardId){
        CommunityBoard targetBoard=communityBoardService.findCommunityBoardEntity(communityBoardId);
        if(targetBoard==null){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        DetailCommunityBoardResponse boardResponse = communityBoardService.getDetailBoardResponse(targetBoard);
        return new ResponseEntity<>(boardResponse,HttpStatus.OK);

        //entity domain 변환 찾아보기
    }

}
