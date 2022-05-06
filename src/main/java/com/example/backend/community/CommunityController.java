package com.example.backend.community;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.dto.*;
import com.example.backend.community.service.CommunityBoardService;
import com.example.backend.community.service.CommunityCommentService;
import com.example.backend.like.LikeService;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final LikeService likeService;
    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<String> createCommunityPost(CommunityBoardDto communityBoardDto){
        //principal 로 유저 정보 받아오는 부분 추가 (회원가입, 로그인 구현 후)
        CommunityBoard post= CommunityBoard.builder()
                .title(communityBoardDto.getTitle()).content(communityBoardDto.getContent()).createdTime(LocalDateTime.now())
                .build();
        communityBoardService.savePost(post,communityBoardDto.getFiles());
        return new ResponseEntity<>("잡담 게시글 등록 성공", HttpStatus.CREATED);
    }

    @PostMapping("/{communityBoardId}/comments")
    public ResponseEntity<String> createCommunityComment(@PathVariable Long communityBoardId, @RequestBody CommunityCommentDto commentDto){
        //principal 추가
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
        Long loginId=1L;
        CommunityBoard targetBoard=communityBoardService.findCommunityBoardEntity(communityBoardId);
        if(targetBoard==null){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        communityBoardService.updateCommunityBoardViewCount(targetBoard);
        DetailCommunityBoardResponse boardResponse = communityBoardService.getDetailBoardResponse(targetBoard,loginId);
        return new ResponseEntity<>(boardResponse,HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<AllBoardResponseWithPageCount> getAllCommunityBoards(@PageableDefault(size=10, sort="id", direction = Sort.Direction.DESC) Pageable pageable){
        int totalPageCount = communityBoardService.getTotalPageCount();
        List<AllCommunityBoardsResponse> allResponsesOfCommunityBoard = communityBoardService.getAllResponsesOfCommunityBoard(pageable);
        return new ResponseEntity<>(new AllBoardResponseWithPageCount(totalPageCount,allResponsesOfCommunityBoard),HttpStatus.OK);
    }

    @GetMapping("best")
    public ResponseEntity<List<AllCommunityBoardsResponse>> getBestCommunityBoards(){
        return new ResponseEntity<>(communityBoardService.getBestResponseOfCommunityBoard(),HttpStatus.OK);
    }

    @PostMapping("/{communityBoardId}/likes")
    public ResponseEntity<Integer> setLikeOnCommunityBoard(@PathVariable Long communityBoardId){
        Long loginId=1L;
        CommunityBoard communityBoard = communityBoardService.findCommunityBoardEntity(communityBoardId);
        User user = userService.findUserById(loginId);
        int likeCount = likeService.saveLike(communityBoard, user);
        communityBoard.updateLikeCount(likeCount);
        return new ResponseEntity<>(likeCount,HttpStatus.OK);
    }

}
