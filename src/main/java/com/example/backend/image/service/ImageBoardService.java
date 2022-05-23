package com.example.backend.image.service;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityBoardImage;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.image.repository.ImageBoardRepository;
import com.example.backend.s3Image.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageBoardService {
    private final ImageBoardRepository imageBoardRepository;
    private final AwsS3Service awsS3Service;

    public void savePost(ImageBoard imageBoard, List<MultipartFile> files){
        imageBoardRepository.save(imageBoard);
    }



}
