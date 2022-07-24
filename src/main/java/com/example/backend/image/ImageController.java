package com.example.backend.image;

import com.example.backend.community.dto.DetailCommunityBoardResponse;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.image.dto.AllImageBoardsResponse;
import com.example.backend.image.dto.AllImageResponseWithPageCount;
import com.example.backend.image.dto.DetailImageBoardResponse;
import com.example.backend.image.dto.ImageBoardDto;
import com.example.backend.image.service.ImageBoardService;
import com.example.backend.like.Like;
import com.example.backend.like.LikeService;
import com.example.backend.report.Report;
import com.example.backend.report.ReportService;
import com.example.backend.s3Image.AwsS3Service;
import com.example.backend.user.UserService;
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
@RequestMapping("/boards/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageBoardService imageBoardService;
    private final LikeService likeService;
    private final UserService userService;
    private final ReportService reportService;
    private final AwsS3Service awsS3Service;

    @PostMapping(value = "",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createImagePost(@RequestPart ImageBoardDto imageBoardDto, @RequestPart List<MultipartFile> files){
        Long loginId = 1L;
        User user = userService.findUserById(loginId);
        System.out.println(imageBoardDto);

        String url = awsS3Service.uploadFile(files.get(0));
        List<String> hashtags = imageBoardDto.getHashtags();
        ImageBoard imageBoard = ImageBoard.builder()
                .imageBoardTitle(imageBoardDto.getImageBoardTitle()).imageUrl(url).createdTime(LocalDateTime.now()).user(user)
                .build();
        imageBoardService.savePost(imageBoard,files);
        imageBoardService.saveImageBoardHashtag(hashtags, imageBoard);
        return new ResponseEntity<>("짤 게시판 등록 성공", HttpStatus.CREATED);
    }

    @GetMapping("/{imageBoardId}")
    public ResponseEntity<DetailImageBoardResponse> getDetailImageBoard(@PathVariable Long imageBoardId){
        Long loginId = 1L;
        ImageBoard targetImageBoard = imageBoardService.findImageBoardEntity(imageBoardId);
        if(targetImageBoard == null){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        imageBoardService.updateImageBoardViewCount(targetImageBoard);
        DetailImageBoardResponse imageBoardResponse = imageBoardService.getDetailImageResponse(targetImageBoard, loginId);
        return new ResponseEntity<>(imageBoardResponse, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<AllImageResponseWithPageCount> getAllImageBoards(@PageableDefault(sort="id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                           @RequestParam(required = false) String q, @RequestParam(required = false) String hashtag){
        return new ResponseEntity<>(imageBoardService.getAllResponseOfImageBoard(pageable,q), HttpStatus.OK);
    }

    @GetMapping("best")
    public ResponseEntity<List<AllImageBoardsResponse>> getBestImageBoards(){
        return new ResponseEntity<>(imageBoardService.getBestResponseOfImageBoard(),HttpStatus.OK);
    }

    @PostMapping("/{imageBoardId}/likes")
    public ResponseEntity<Integer> setLikeOnImageBoard(@PathVariable Long imageBoardId){
        Long loginId = 1L;
        ImageBoard imageBoard = imageBoardService.findImageBoardEntity(imageBoardId);
        User user = userService.findUserById(loginId);
        Like like = likeService.findExistingLike(imageBoard,user);
        Integer totalLikeCount;
        if(like==null){
            like = Like.builder()
                    .imageBoard(imageBoard).user(user)
                    .build();
            totalLikeCount = imageBoard.updateLikeCount(1);
        }
        else{
            Boolean likeStatus = like.modifyLikeStatus();
            totalLikeCount = imageBoard.updateLikeCount(likeStatus?1:-1);
        }
        likeService.saveLike(like);
        return new ResponseEntity<>(totalLikeCount,HttpStatus.OK);
    }

    @PostMapping("/{imageBoardId}/reports")
    public ResponseEntity<String> reportImageBoard(@PathVariable Long imageBoardId){
        Long loginId = 5L;
        ImageBoard imageBoard = imageBoardService.findImageBoardEntity(imageBoardId);
        User user = userService.findUserById(loginId);
        if(reportService.checkReportExistence(user,imageBoard)){
            return new ResponseEntity<>("해당 사용자가 이미 신고한 짤",HttpStatus.OK);
        }
        Report report = Report.builder().imageBoard(imageBoard).user(user).build();
        reportService.saveReport(report);
        if(imageBoard.getReports().size()>=5){
            imageBoardService.deleteImageBoard(imageBoard);
            return new ResponseEntity<>("신고 5번 누적으로 삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("짤 게시글 신고 성공",HttpStatus.OK);
    }

    @DeleteMapping("/{imageBoardId}")
    public ResponseEntity<String> deleteImageBoard(@PathVariable Long imageBoardId){
        ImageBoard imageBoard = imageBoardService.findImageBoardEntity(imageBoardId);
        imageBoardService.deleteImageBoard(imageBoard);
        return new ResponseEntity<>("짤 게시글 삭제 성공",HttpStatus.NO_CONTENT);
    }
}
