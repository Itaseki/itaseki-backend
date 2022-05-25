package com.example.backend.community.service;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityBoardImage;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.dto.AllBoardResponseWithPageCount;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        imageUrls = images.stream()
                .map(CommunityBoardImage::getImageUrl)
                .collect(Collectors.toList());

        return imageUrls;
    }

    public CommunityBoard findCommunityBoardEntity(Long boardId){
        Optional<CommunityBoard> board = communityBoardRepository.findById(boardId);
        if(board.isPresent()&&board.get().getStatus()){
            return board.get();
        }
        return null;
    }

    public DetailCommunityBoardResponse getDetailBoardResponse(CommunityBoard communityBoard,Long loginId){
        Long boardWriterId=communityBoard.getUser().getUserId();
        List<CommunityComment> allComments= communityBoard.getComments();
        List<CommunityComment> parentComments=allComments
                        .stream()
                        .filter(comment->comment.getIsParentComment().equals(true))
                        .collect(Collectors.toList());
        List<CommunityCommentsResponse> comments=commentService.getCommentsResponses(parentComments,loginId,boardWriterId);
        List<String> images=this.getImageUrlsInPost(communityBoard);
        return DetailCommunityBoardResponse.fromEntity(communityBoard,comments,images,loginId,commentService.getAllCommentsOnBoard(communityBoard).size());
    }

    public void updateCommunityBoardViewCount(CommunityBoard board){
        board.updateViewCount();
        communityBoardRepository.save(board);
    }

    public AllBoardResponseWithPageCount getAllResponsesOfCommunityBoard(Pageable pageable, String query){
        String[] queryList=null;
        if(query!=null){
            queryList= query.split(" ");
        }
        Page<CommunityBoard> boardPages = communityBoardRepository.findAll(pageable, queryList);
        List<AllCommunityBoardsResponse> boardResponses = toAllCommunityBoardResponse(boardPages.getContent());
        return new AllBoardResponseWithPageCount(boardPages.getTotalPages(), boardResponses);
    }

    public List<AllCommunityBoardsResponse> getBestResponseOfCommunityBoard(){
        return toAllCommunityBoardResponse(communityBoardRepository.findBestBoards());
    }

    private List<AllCommunityBoardsResponse> toAllCommunityBoardResponse(List<CommunityBoard> boards){
        return boards.stream()
                .filter(board->board.getStatus().equals(true))
                .map(board->AllCommunityBoardsResponse.fromEntity(board,commentService.getAllCommentsOnBoard(board).size()))
                .collect(Collectors.toList());
    }

    public void deleteCommunityBoard(CommunityBoard board){
        board.setStatus(false);
        communityBoardRepository.save(board);
    }
}
