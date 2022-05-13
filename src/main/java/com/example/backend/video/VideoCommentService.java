package com.example.backend.video;

import com.example.backend.video.domain.VideoComment;
import com.example.backend.video.repository.VideoCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

}
