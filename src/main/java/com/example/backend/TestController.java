package com.example.backend;

import com.example.backend.community.CommunityPost;
import com.example.backend.community.CommunityPostDto;
import com.example.backend.community.CommunityPostService;
import com.example.backend.s3Image.AwsS3Service;
import com.example.backend.s3Image.TestUploadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
//s3 연결, mysql 연결 테스트용 컨트롤러 -> 추후 삭제
public class TestController {
    private final AwsS3Service s3Service;
    private final CommunityPostService communityPostService;

    @PostMapping("/test")
    public String uploadFile(TestUploadDto testUploadDto){
        System.out.println("controller called");
        String category= testUploadDto.getCategory();
        MultipartFile file= testUploadDto.getFile();

        return s3Service.uploadFile(category, file);
    }

//    @GetMapping("/test")
//    public String getFile(@RequestParam String fileName){
//        return s3Service.findFile(fileName);
//    }

    @PostMapping("/test-db")
    public String uploadData(@RequestBody CommunityPostDto postDto){
        CommunityPost post=CommunityPost.builder()
                .title(postDto.getTitle()).content(postDto.getContent()).createdTime(LocalDateTime.now())
                .build();
        communityPostService.savePost(post);
        return "success";
    }

    @GetMapping("/test-db/{communityPostId}")
    public ResponseEntity<CommunityPost> getData(@PathVariable Long communityPostId){
        CommunityPost post = communityPostService.findPostById(communityPostId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

}
