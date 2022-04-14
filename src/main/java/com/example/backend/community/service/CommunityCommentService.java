package com.example.backend.community.service;

import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.repository.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityCommentService {
    private final CommunityCommentRepository commentRepository;

    public String saveCommunityComment(CommunityComment comment,Long parentId){
        CommunityComment savedComment = commentRepository.save(comment);
        if(parentId!=0&& !checkParentComment(parentId)){
            return "fail";
        }
        setCommentGroupId(savedComment, parentId);
        return "success";
    }

    private void setCommentGroupId(CommunityComment comment, Long parentId){
        if(parentId==0){
            comment.setCommentGroupId(comment.getId());
        }else{
            comment.setCommentGroupId(parentId);
        }
        commentRepository.save(comment);
    }

    private Boolean checkParentComment(Long parentId){
        Optional<CommunityComment> parentComment = commentRepository.findById(parentId);
        return parentComment.isPresent();

    }
}
