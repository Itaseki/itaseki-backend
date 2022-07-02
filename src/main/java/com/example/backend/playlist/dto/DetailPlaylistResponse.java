package com.example.backend.playlist.dto;

import com.example.backend.playlist.domain.Playlist;
import com.example.backend.user.domain.User;
import com.example.backend.video.dto.PlaylistVideoResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class DetailPlaylistResponse {
    private Long id;
    private String title;
    private LocalDateTime createdTime;
    private Integer viewCount;
    private Integer likeCount;
    private Long writerId;
    private String writerNickname;
    private Boolean isThisUserWriter;
    private Integer commentCount;
    private List<PlaylistVideoResponse> videos;
    private List<PlaylistCommentsResponse> comments;


    public static DetailPlaylistResponse fromEntity(Playlist playlist, List<PlaylistCommentsResponse> comments, List<PlaylistVideoResponse> videos, Long loginId){
//        User 객체, 요청사용자 id 파라미터 추가 + builder 패턴에 추가
        User playlistWriter=playlist.getUser();
        return DetailPlaylistResponse.builder()
                .id(playlist.getId()).title(playlist.getTitle()).commentCount(getPlaylistCommentCount(comments))
                .createdTime(playlist.getCreatedTime()).viewCount(playlist.getViewCount()).likeCount(playlist.getLikeCount())
                .comments(comments)
                .writerId(playlistWriter.getUserId()).writerNickname(playlistWriter.getNickname()).isThisUserWriter(playlistWriter.getUserId().equals(loginId))
                .videos(videos)
                .build();
    }

    private static Integer getPlaylistCommentCount(List<PlaylistCommentsResponse> comments){
        if(comments==null)
            return 0;
        int count=comments.size();
        for(PlaylistCommentsResponse r: comments){
            if(r.getNestedComments()==null)
                continue;
            count+=r.getNestedComments().size();
        }
        return count;
    }
}
