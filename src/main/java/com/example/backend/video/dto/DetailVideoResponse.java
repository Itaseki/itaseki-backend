package com.example.backend.video.dto;

import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class DetailVideoResponse {
    private Long id;
    private  String description;
    private String videoTitle;
    private String url;
    private String videoUploader;
    private String series;
    private Integer episode;
    private List<String> hashtags;
    private LocalDateTime createdTime;
    private Integer viewCount;
    private Integer likeCount;
    private Long writerId;
    private String writerNickname;
    private Boolean isThisUserWriter;
    private Integer commentCount;
    private List<VideoCommentResponse> comments;

    public static DetailVideoResponse fromEntity(Video video, List<VideoCommentResponse> comments, Long loginId, List<String> hashtags){
//        hashtag 리스트 받아서 저장
        User boardWriter=video.getUser();
        return DetailVideoResponse.builder()
                .id(video.getId())
                .description(video.getDescription())
                .videoTitle(video.getOriginVideoTitle())
                .createdTime(video.getCreatedTime())
                .viewCount(video.getViewCount())
                .likeCount(video.getLikeCount())
                .comments(comments)
                .series(video.getSeries().getSeriesName())
                .url(video.getVideoUrl())
                .episode(video.getEpisodeNumber())
                .hashtags(hashtags)
                .writerId(boardWriter.getUserId())
                .writerNickname(boardWriter.getNickname())
                .isThisUserWriter(boardWriter.getUserId().equals(loginId))
                .videoUploader(video.getVideoUploader())
                .commentCount(calculateCommentCount(comments))
                .build();
    }

    private static int calculateCommentCount(List<VideoCommentResponse> comments){
        if (comments == null) {
            return 0;
        }
        int count=comments.size();
        for (VideoCommentResponse comment : comments) {
            if (comment.getNestedComments() == null) {
                continue;
            }
            count += comment.getNestedComments().size();
        }
        return count;
    }

}
