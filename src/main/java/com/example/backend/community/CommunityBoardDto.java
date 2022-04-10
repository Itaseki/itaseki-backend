package com.example.backend.community;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CommunityBoardDto {
    private String title;
    private String content;
    private List<MultipartFile> files;
}
