package com.example.backend.community.service;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.dto.CommunityCommentsResponse;
import com.example.backend.community.repository.CommunityCommentRepository;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityCommentService {
    private final CommunityCommentRepository commentRepository;

    public void saveCommunityComment(CommunityComment comment,Long parentId){
        CommunityComment parentComment = checkParentComment(parentId);
        if(parentComment!=null){
            comment.setParentComment(parentComment);
        }
        commentRepository.save(comment);
    }

    public CommunityComment findCommunityCommentById(Long commentId){
        Optional<CommunityComment> comment = commentRepository.findById(commentId);
        return comment.filter(CommunityComment::getStatus).orElse(null);
    }

    private CommunityComment checkParentComment(Long parentId){
        if(parentId.equals(0L))
            return null;
        Optional<CommunityComment> parentComment = commentRepository.findById(parentId);
        return parentComment.orElse(null);

    }

    public List<CommunityCommentsResponse> getCommentsResponses(List<CommunityComment> comments,Long loginId,Long boardWriterId){
        List<CommunityCommentsResponse> responses=new ArrayList<>();

        for(CommunityComment comment:comments){
            responses.add(toCommentResponse(comment,boardWriterId,loginId));
        }
        return responses;
    }


    private CommunityCommentsResponse toCommentResponse(CommunityComment comment,Long boardWriterId,Long loginId){

        CommunityCommentsResponse response = CommunityCommentsResponse.fromEntity(comment,boardWriterId,loginId);
        if(comment.getIsParentComment()){
            List<CommunityComment> childComments = comment.getChildComments();
            List<CommunityCommentsResponse> childResponses=new ArrayList<>();
            for(CommunityComment child:childComments){
                if(!child.getStatus()){ //status 가 false, 즉 지워진 대댓글이라면 저장 안하고 continue
                    continue;
                }
                childResponses.add(toCommentResponse(child,boardWriterId,loginId));
            }
            response.setNestedComments(childResponses);
        }else{
            response.setNestedComments(null);
        }
        return response;
    }

    public void deleteCommunityComment(CommunityComment comment){
        comment.setStatus(false);
        commentRepository.save(comment);
    }

    public List<CommunityComment> getAllCommentsOnBoard(CommunityBoard board){
        return commentRepository.findAllCommentsOnBoard(board);
    }
}
