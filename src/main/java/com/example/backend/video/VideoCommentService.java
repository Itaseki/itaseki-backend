package com.example.backend.video;

import com.example.backend.video.domain.VideoComment;
import com.example.backend.video.dto.VideoCommentsResponse;
import com.example.backend.video.repository.VideoCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
                .collect(Collectors.toList());

    }

    private VideoCommentsResponse toVideoCommentResponse(VideoComment comment, Long boardWriterId, Long loginId){
        VideoCommentsResponse response = VideoCommentsResponse.fromEntity(comment, boardWriterId, loginId);
        if(!comment.getIsParentComment()){
            response.setNestedComments(null);
        }else{
            List<VideoComment> childComments = comment.getChildComments();
            //각 부모 댓글들의 자식 댓글들 세팅
            response.setNestedComments(childComments.stream()
                            .filter(childComment->childComment.getStatus().equals(true)) //자식댓글들 중, status true인 애들만 세팅
                            .map(childComment -> VideoCommentsResponse.fromEntity(childComment, boardWriterId, loginId))
                            .collect(Collectors.toList()));
        }
        return response;
    }

}
