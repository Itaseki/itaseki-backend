package com.example.backend.video.service;

import com.example.backend.video.domain.VideoComment;
import com.example.backend.video.dto.VideoCommentsResponse;
import com.example.backend.video.exception.NoSuchCommentException;
import com.example.backend.video.repository.VideoCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoCommentService {
    private final VideoCommentRepository commentRepository;

    public void saveVideoComment(VideoComment comment, Long parentId){
        VideoComment parentComment = checkParentComment(parentId);
        if(parentComment!=null)
            comment.setParentComment(parentComment);
        commentRepository.save(comment);
    }

    private VideoComment checkParentComment(Long parentId){
        if(parentId.equals(0L))
            return null;
        Optional<VideoComment> comment = commentRepository.findById(parentId);
        if(comment.isPresent()&&comment.get().getStatus())
            return comment.get();
        return null;
    }

    public List<VideoCommentsResponse> getVideoCommentResponses(List<VideoComment> comments,Long loginId, Long boardWriterId){
        return comments.stream()
                .map(comment->toVideoCommentResponse(comment,boardWriterId,loginId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    private VideoCommentsResponse toVideoCommentResponse(VideoComment comment, Long boardWriterId, Long loginId){
        VideoCommentsResponse response = VideoCommentsResponse.fromEntity(comment, boardWriterId, loginId);
        if(!comment.getIsParentComment()){
            response.setNestedComments(null);
        }else{
            List<VideoComment> childComments = comment.getChildComments();
            //각 부모 댓글들의 자식 댓글들 세팅
            List<VideoCommentsResponse> childResponses = childComments.stream()
                    .filter(childComment -> childComment.getStatus().equals(true)) //자식댓글들 중, status true인 애들만 세팅
                    .map(childComment -> VideoCommentsResponse.fromEntity(childComment, boardWriterId, loginId))
                    .collect(Collectors.toList());
            if(!comment.getStatus()){
                if(childResponses.isEmpty())
                    return null;
                response.setContent("삭제된 댓글입니다");
            }
            response.setNestedComments(childResponses);
        }
        return response;
    }

    public VideoComment findVideoCommentById(Long id) {
        Optional<VideoComment> comment = commentRepository.findById(id);
        if(comment.isPresent() && comment.get().getStatus()){
            return comment.get();
        }
        throw new NoSuchCommentException();
    }

    public void deleteVideoComment(VideoComment comment){
        comment.setStatus(false);
        commentRepository.save(comment);
    }

}
