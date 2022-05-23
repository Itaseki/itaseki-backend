package com.example.backend.image;

import com.example.backend.community.dto.DetailCommunityBoardResponse;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.image.dto.DetailImageBoardResponse;
import com.example.backend.image.dto.ImageBoardDto;
import com.example.backend.image.service.ImageBoardService;
import com.example.backend.like.LikeService;
import com.example.backend.report.ReportService;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/boards/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageBoardService imageBoardService;
    private final LikeService likeService;
    private UserService userService;
    private final ReportService reportService;

    @PostMapping("")
    public ResponseEntity<String> createImagePost(ImageBoardDto imageBoardDto){
        Long loginId = 1L;
        User user = userService.findUserById(loginId);
        ImageBoard imageBoard = ImageBoard.builder()
                .imageBoardTitle(imageBoardDto.getImageBoardTitle()).imageUrl(imageBoardDto.getImageUrl()).createdTime(LocalDateTime.now()).user(user)
                .build();
        imageBoardService.savePost(imageBoard);
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
}
