package com.example.backend.image.service;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityBoardImage;
import com.example.backend.community.dto.AllBoardResponseWithPageCount;
import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.customHashtag.CustomHashtagRepository;
import com.example.backend.customHashtag.QCustomHashtag;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.image.dto.AllImageBoardsResponse;
import com.example.backend.image.dto.AllImageResponseWithPageCount;
import com.example.backend.image.dto.DetailImageBoardResponse;
import com.example.backend.image.repository.ImageBoardRepository;
import com.example.backend.s3Image.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageBoardService {
    private final ImageBoardRepository imageBoardRepository;
    private final AwsS3Service awsS3Service;
    private final CustomHashtagRepository customHashtagRepository;

    public void savePost(ImageBoard imageBoard){
        imageBoardRepository.save(imageBoard);
    }


    public void deleteImageBoard(ImageBoard imageBoard){
        imageBoard.setStatus(false);
        imageBoardRepository.save(imageBoard);

        System.out.println(imageBoard.getId());
        List<CustomHashtag> hashtags = customHashtagRepository.findByImageBoard_Id(imageBoard.getId());

        for (CustomHashtag hashtag : hashtags) {
            customHashtagRepository.delete(hashtag);
        }
    }

    public ImageBoard findImageBoardEntity(Long imageBoardId){
        Optional<ImageBoard> imageBoard = imageBoardRepository.findById(imageBoardId);
        if(imageBoard.isPresent()&&imageBoard.get().getStatus()){
            return imageBoard.get();
        }
        return null;
    }

    public void updateImageBoardViewCount(ImageBoard imageBoard){
        imageBoard.updateViewCount();
        imageBoardRepository.save(imageBoard);
    }

    public List<AllImageBoardsResponse> getBestResponseOfImageBoard(){
        return toAllImageBoardResponse(imageBoardRepository.findBestBoards());
    }

    private List<AllImageBoardsResponse> toAllImageBoardResponse(List<ImageBoard> imageBoards){
        return imageBoards.stream()
                .filter(imageBoard -> imageBoard.getStatus().equals(true))
                .map(AllImageBoardsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public String getImageUrlInPost(ImageBoard imageBoard){
        return imageBoard.getImageUrl();
    }

    public DetailImageBoardResponse getDetailImageResponse(ImageBoard imageBoard, Long loginId){
        Long imageWriterId = imageBoard.getUser().getUserId();
        return DetailImageBoardResponse.fromEntity(imageBoard, loginId, getHashtagKeywordStringInImageBoard(imageBoard));
    }

    public AllImageResponseWithPageCount getAllResponseOfImageBoard(Pageable pageable){
        Page<ImageBoard> imageBoardPage = imageBoardRepository.findAll(pageable);
        List<AllImageBoardsResponse> imageBoardsResponses = toAllImageBoardResponse(imageBoardPage.getContent());
        return new AllImageResponseWithPageCount(imageBoardPage.getTotalPages(),imageBoardsResponses);
    }

    public void saveImageBoardHashtag(List<String> hashtags, ImageBoard imageBoard){
        int order = 1;
        for(String hashtag : hashtags){
            CustomHashtag imageBoardHashtag = CustomHashtag.builder().imageBoard(imageBoard).name(hashtag).order(order++).build();
            customHashtagRepository.save(imageBoardHashtag);
        }
    }

    private List<String> getHashtagKeywordStringInImageBoard(ImageBoard imageBoard){
        List<CustomHashtag> customHashtags = imageBoard.getCustomHashtags();
        return customHashtags.stream().map(CustomHashtag::getCustomHashtagName).collect(Collectors.toList());
    }

}
