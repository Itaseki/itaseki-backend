package com.example.backend.community.service;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityBoardImage;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.dto.AllCommunityBoardsResponse;
import com.example.backend.community.dto.CommunityCommentsResponse;
import com.example.backend.community.dto.DetailCommunityBoardResponse;
import com.example.backend.community.repository.CommunityBoardImageRepository;
import com.example.backend.community.repository.CommunityBoardRepository;
import com.example.backend.s3Image.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final CommunityCommentService commentService;

    public void savePost(CommunityBoard communityBoard, List<MultipartFile> files){
        communityBoardRepository.save(communityBoard);
        savePostImages(files,communityBoard);
    }


    private void savePostImages(List<MultipartFile> files,CommunityBoard board){
        if(files==null){
            return;
        }
        for(int i=0;i<files.size();i++){
            MultipartFile file=files.get(i);
            String originName=file.getOriginalFilename();
            String url = awsS3Service.uploadFile(file);
            CommunityBoardImage boardImage=CommunityBoardImage.builder()
                            .board(board).fileName(originName).url(url).order(i+1).build();
            communityImageRepository.save(boardImage);
        }
    }

    public List<String> getImageUrlsInPost(CommunityBoard communityBoard){
        List<CommunityBoardImage> images = communityBoard.getImages();
        List<String> imageUrls=new ArrayList<>();
        if(images==null){
            return imageUrls;
        }
        for(CommunityBoardImage image:images){
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }

    public CommunityBoard findCommunityBoardEntity(Long boardId){
        Optional<CommunityBoard> board = communityBoardRepository.findById(boardId);
        return board.orElse(null);
    }

    public DetailCommunityBoardResponse getDetailBoardResponse(CommunityBoard communityBoard,Long loginId){
        List<CommunityComment> originComments=communityBoard.getComments();
        originComments.removeIf(comment -> !comment.getIsParentComment()); //Iterator 을 사용한 remove statement 를 Collections.removeIf로 단순화
        List<CommunityCommentsResponse> comments=commentService.getCommentsResponses(originComments,loginId);
        List<String> images=this.getImageUrlsInPost(communityBoard);
        return DetailCommunityBoardResponse.fromEntity(communityBoard,comments,images,loginId);
    }

    public void updateCommunityBoardViewCount(CommunityBoard board){
        board.updateViewCount();
        communityBoardRepository.save(board);
    }

    public List<AllCommunityBoardsResponse> getAllResponsesOfCommunityBoard(Pageable pageable){
        Page<CommunityBoard> boardPages = communityBoardRepository.findAll(pageable);
        List<AllCommunityBoardsResponse> responses=new ArrayList<>();
        for(CommunityBoard board : boardPages){
            responses.add(AllCommunityBoardsResponse.fromEntity(board));
        }
        return responses;
    }

}
