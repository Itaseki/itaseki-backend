package com.example.backend.community;

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

    public void savePost(CommunityBoard communityBoard){
        communityBoardRepository.save(communityBoard);
    }

    public List<String> savePostImages(List<MultipartFile> files){
        List<String> savedUrls=new ArrayList<>();
        for(MultipartFile file:files){
            String url = awsS3Service.uploadFile(file);
            savedUrls.add(url); //list 저장 -> DB 저장으로 변경
        }
        return savedUrls;
    }

    public CommunityBoard findPostById(Long id){
        Optional<CommunityBoard> post = communityBoardRepository.findById(id);
        return post.orElse(null);
    }

}
