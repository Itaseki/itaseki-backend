package com.example.backend.community;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.dto.*;
import com.example.backend.community.service.CommunityBoardService;
import com.example.backend.community.service.CommunityCommentService;
import com.example.backend.like.Like;
import com.example.backend.like.LikeService;
import com.example.backend.report.Report;
import com.example.backend.report.ReportService;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final ReportService reportService;

    @PostMapping(value = "",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createCommunityPost(@RequestPart CommunityBoardDto communityBoardDto,
                                                      @RequestPart(required = false) List<MultipartFile> files){
        //principal 로 유저 정보 받아오는 부분 추가 (회원가입, 로그인 구현 후)
        Long loginId=3L;
        User user=userService.findUserById(loginId);
        System.out.println("board content: "+communityBoardDto.getContent());
        CommunityBoard post= CommunityBoard.builder()
                .title(communityBoardDto.getTitle()).content(communityBoardDto.getContent()).createdTime(LocalDateTime.now()).user(user)
                .build();
        communityBoardService.savePost(post,files);
        return new ResponseEntity<>("잡담 게시글 등록 성공", HttpStatus.CREATED);
    }

    @PostMapping("/{communityBoardId}/comments")
    public ResponseEntity<String> createCommunityComment(@PathVariable Long communityBoardId, @RequestBody CommunityCommentDto commentDto){
        //principal 추가
        Long loginId=1L;
        User user=userService.findUserById(loginId);
        CommunityBoard targetBoard=communityBoardService.findCommunityBoardEntity(communityBoardId);
        if(targetBoard==null){
            return new ResponseEntity<>("존재하지 않는 게시글에 대한 댓글 등록 요청",HttpStatus.NOT_FOUND);
        }
        CommunityComment comment=CommunityComment.builder()
                .content(commentDto.getContent()).parentId(commentDto.getParentCommentId())
                .createdTime(LocalDateTime.now()).communityBoard(targetBoard).user(user)
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
    public ResponseEntity<AllBoardResponseWithPageCount> getAllCommunityBoards(@PageableDefault(sort="id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                               @RequestParam(required = false) String q){
        return new ResponseEntity<>(communityBoardService.getAllResponsesOfCommunityBoard(pageable,q),HttpStatus.OK);
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
        Like like = likeService.findExistingLike(communityBoard, user);
        Integer totalLikeCount;
        if(like==null){
            like=Like.builder()
                    .communityBoard(communityBoard).user(user)
                    .build();
            totalLikeCount=communityBoard.updateLikeCount(1);
        }else{
            Boolean likeStatus = like.modifyLikeStatus();
            totalLikeCount= communityBoard.updateLikeCount(likeStatus?1:-1);
        }
        likeService.saveLike(like);
        return new ResponseEntity<>(totalLikeCount,HttpStatus.OK);
    }

    @PostMapping("/{communityBoardId}/reports")
    public ResponseEntity<String> reportCommunityBoard(@PathVariable Long communityBoardId){
        Long loginId=2L;
        CommunityBoard communityBoard = communityBoardService.findCommunityBoardEntity(communityBoardId);
        User user=userService.findUserById(loginId);
        if(reportService.checkReportExistence(user,communityBoard)){
            return new ResponseEntity<>("해당 사용자가 이미 신고한 잡담글",HttpStatus.OK);
        }
        Report report = Report.builder().communityBoard(communityBoard).user(user).build();
        reportService.saveReport(report, communityBoard.getUser());
        if(communityBoard.getReports().size()>=5){
            communityBoardService.deleteCommunityBoard(communityBoard); //삭제하기 보다는 그냥 status 를 0으로 바꿔둘까,,?
            return new ResponseEntity<>("신고 5번 누적으로 삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("잡담글 신고 성공",HttpStatus.OK);
    }

    @PostMapping("/{communityBoardId}/comments/{communityCommentId}/reports")
    public ResponseEntity<String> reportCommunityComment(@PathVariable Long communityBoardId, @PathVariable Long communityCommentId){
        Long loginId=3L;
        CommunityComment comment = commentService.findCommunityCommentById(communityCommentId);
        User user=userService.findUserById(loginId);
        if(reportService.checkReportExistence(user,comment)){
            return new ResponseEntity<>("해당 사용자가 이미 신고한 잡담댓글",HttpStatus.OK);
        }
        Report report = Report.builder().communityComment(comment).user(user).build();
        reportService.saveReport(report, comment.getUser());
        if(comment.getReports().size()>=5){
            commentService.deleteCommunityComment(comment); //삭제하기 보다는 그냥 status 를 0으로 바꿔둘까,,?
            return new ResponseEntity<>("신고 5번 누적으로 삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("잡담댓글 신고 성공",HttpStatus.OK);
    }

    @DeleteMapping("/{communityBoardId}")
    public ResponseEntity<String> deleteCommunityBoard(@PathVariable Long communityBoardId){
        CommunityBoard boardEntity = communityBoardService.findCommunityBoardEntity(communityBoardId);
        communityBoardService.deleteCommunityBoard(boardEntity);
        return new ResponseEntity<>("잡담글 삭제 성공",HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{communityBoardId}/comments/{communityCommentId}")
    public ResponseEntity<String> deleteCommunityComment(@PathVariable Long communityCommentId){
        CommunityComment comment = commentService.findCommunityCommentById(communityCommentId);
        commentService.deleteCommunityComment(comment);
        return new ResponseEntity<>("댓글 삭제 성공",HttpStatus.NO_CONTENT);
    }

}
