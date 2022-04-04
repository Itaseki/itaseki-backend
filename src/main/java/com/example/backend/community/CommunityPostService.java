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
public class CommunityPostService {
    private final CommunityPostRepository communityPostRepository;
    private final AwsS3Service awsS3Service;

    public void savePost(CommunityPost communityPost){
        communityPostRepository.save(communityPost);
    }

    public List<String> savePostImages(List<MultipartFile> files){
        List<String> savedUrls=new ArrayList<>();
        for(MultipartFile file:files){
            String url = awsS3Service.uploadFile(file);
            savedUrls.add(url); //list 저장 -> DB 저장으로 변경
        }
        return savedUrls;
    }

    public CommunityPost findPostById(Long id){
        Optional<CommunityPost> post = communityPostRepository.findById(id);
        return post.orElse(null);
    }

}
