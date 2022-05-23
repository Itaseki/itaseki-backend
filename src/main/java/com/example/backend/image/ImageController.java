package com.example.backend.image;

import com.example.backend.image.service.ImageBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageBoardService imageBoardService;
}
