package com.example.backend.community.service;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityBoardImage;
import com.example.backend.community.repository.CommunityBoardImageRepository;
import com.example.backend.community.repository.CommunityBoardRepository;
import com.example.backend.s3Image.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityBoardService {
    private final CommunityBoardRepository communityBoardRepository;
    private final AwsS3Service awsS3Service;
    private final CommunityBoardImageRepository communityImageRepository;

    public void savePost(CommunityBoard communityBoard, List<MultipartFile> files){
        communityBoardRepository.save(communityBoard);
        savePostImages(files,communityBoard);
    }


    private void savePostImages(List<MultipartFile> files,CommunityBoard board){
        for(int i=0;i<files.size();i++){
            MultipartFile file=files.get(i);
            String originName=file.getOriginalFilename();
            String url = awsS3Service.uploadFile(file);
            CommunityBoardImage boardImage=CommunityBoardImage.builder()
                            .board(board).fileName(originName).url(url).order(i+1).build();
            communityImageRepository.save(boardImage);
        }
    }

}
