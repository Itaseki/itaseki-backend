package com.example.backend.playlist.service;

import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.playlist.repository.PlaylistCommentRepository;
import com.example.backend.playlist.dto.PlaylistCommentsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistCommentService {
    private final PlaylistCommentRepository commentRepository;

    public void savePlaylistComment(PlaylistComment comment, Long parentId){
        PlaylistComment parentComment = checkParentComment(parentId);
        if(parentComment!=null)
            comment.setParentComment(parentComment);
        commentRepository.save(comment);
    }

    private PlaylistComment checkParentComment(Long parentId){
        if(parentId.equals(0L))
            return null;
        Optional<PlaylistComment> comment = commentRepository.findById(parentId);
        if(comment.isPresent()&&comment.get().getStatus())
            return comment.get();
        return null;
    }

    public List<PlaylistCommentsResponse> getPlaylistCommentResponses(List<PlaylistComment> comments, Long loginId, Long boardWriterId){
        return comments.stream()
                .map(comment->toPlaylistCommentResponse(comment,boardWriterId,loginId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    private PlaylistCommentsResponse toPlaylistCommentResponse(PlaylistComment comment, Long boardWriterId, Long loginId){
        PlaylistCommentsResponse response = PlaylistCommentsResponse.fromEntity(comment, boardWriterId, loginId);
        if(!comment.getIsParentComment()){
            response.setNestedComments(null);
        }else{
            List<PlaylistComment> childComments = comment.getChildComments();
            //각 부모 댓글들의 자식 댓글들 세팅
            List<PlaylistCommentsResponse> childResponses = childComments.stream()
                    .filter(childComment -> childComment.getStatus().equals(true)) //자식댓글들 중, status true인 애들만 세팅
                    .map(childComment -> PlaylistCommentsResponse.fromEntity(childComment, boardWriterId, loginId))
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
}
