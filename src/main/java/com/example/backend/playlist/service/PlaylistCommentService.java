package com.example.backend.playlist.service;

import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.playlist.repository.PlaylistCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
