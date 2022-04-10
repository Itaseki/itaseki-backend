package com.example.backend.community;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.dto.CommunityBoardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/boards/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityBoardService communityBoardService;

    @PostMapping("")
    public ResponseEntity<String> createCommunityPost(CommunityBoardDto communityBoardDto){
        CommunityBoard post= CommunityBoard.builder()
                .title(communityBoardDto.getTitle()).content(communityBoardDto.getContent()).createdTime(LocalDateTime.now())
                .build();
        communityBoardService.savePost(post,communityBoardDto.getFiles());
        return new ResponseEntity<>("잡담 게시글 등록 성공", HttpStatus.CREATED);
    }
}
