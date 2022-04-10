package com.example.backend.s3Image;

import com.example.backend.community.CommunityBoard;
import com.example.backend.community.CommunityBoardDto;
import com.example.backend.community.CommunityBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
//s3 연결, mysql 연결 테스트용 컨트롤러 -> 추후 삭제
public class TestController {
    private final AwsS3Service s3Service;
    private final CommunityBoardService communityBoardService;

    //s3 서버 이미지 업로드 테스트
    @PostMapping("/test")
    public String uploadFile(TestUploadDto testUploadDto){
        String category= testUploadDto.getCategory();
        MultipartFile file= testUploadDto.getFile();

        return s3Service.uploadFile(file);
    }

//    @GetMapping("/test")
//    public String getFile(@RequestParam String fileName){
//        return s3Service.findFile(fileName);
//    }

    //게시글 + 이미지 리스트 업로드 + 디비 반영 테스트
    @PostMapping("/test-db")
    public ResponseEntity<List<String>> uploadData(CommunityBoardDto postDto){
        CommunityBoard post= CommunityBoard.builder()
                .title(postDto.getTitle()).content(postDto.getContent()).createdTime(LocalDateTime.now())
                .build();
        communityBoardService.savePost(post);

        List<MultipartFile> files = postDto.getFiles();
        List<String> savedUrls= communityBoardService.savePostImages(files);

        return new ResponseEntity<>(savedUrls,HttpStatus.CREATED);
    }

    //디비 조회 테스트
    @GetMapping("/test-db/{communityPostId}")
    public ResponseEntity<CommunityBoard> getData(@PathVariable Long communityPostId){
        CommunityBoard post = communityBoardService.findPostById(communityPostId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

}
