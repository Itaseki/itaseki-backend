package com.example.backend.image.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageBoardDto {
    private String imageBoardTitle;
    private String imageUrl;
    private List<String> hashtags;
}
