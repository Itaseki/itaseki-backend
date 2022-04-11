package com.example.backend.community.service;

import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.repository.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityCommentService {
    private final CommunityCommentRepository commentRepository;

    public void saveCommunityComment(CommunityComment comment,Long parentId){
        CommunityComment savedComment = commentRepository.save(comment);
        setCommentGroupId(savedComment, parentId);
    }

    public void setCommentGroupId(CommunityComment comment, Long parentId){
        if(parentId==0){
            comment.setCommentGroupId(comment.getId());
        }else{
            comment.setCommentGroupId(parentId);
        }
        commentRepository.save(comment);
    }
}
