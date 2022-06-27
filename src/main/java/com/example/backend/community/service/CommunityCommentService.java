package com.example.backend.community.service;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.community.dto.CommunityCommentsResponse;
import com.example.backend.community.repository.CommunityCommentRepository;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

        return comments.stream()
                .map(comment -> toCommentResponse(comment, boardWriterId, loginId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private CommunityCommentsResponse toCommentResponse(CommunityComment comment,Long boardWriterId,Long loginId){

        CommunityCommentsResponse response = CommunityCommentsResponse.fromEntity(comment,boardWriterId,loginId);
        if(comment.getIsParentComment()){
            List<CommunityComment> childComments = comment.getChildComments();

            List<CommunityCommentsResponse> childResponses = childComments.stream()
                    .filter(CommunityComment::getStatus)
                    .map(child->toCommentResponse(child,boardWriterId,loginId))
                    .collect(Collectors.toList());

            if(!comment.getStatus()){
                if(childResponses.isEmpty()) //자식 대댓글이 없는, 삭제된 댓글이라면 null
                    return null;
                response.setContent("삭제된 댓글입니다");//자식 대댓글이 있는, 삭제된 댓글이라면 "삭제된 댓글 표시"
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
