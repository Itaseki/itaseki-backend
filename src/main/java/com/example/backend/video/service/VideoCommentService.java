package com.example.backend.video.service;

import com.example.backend.report.ReportService;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import com.example.backend.video.dto.VideoCommentResponse;
import com.example.backend.video.exception.NoSuchCommentException;
import com.example.backend.video.exception.WrongParentCommentException;
import com.example.backend.video.repository.VideoCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoCommentService {
    private final VideoCommentRepository commentRepository;
    private final ReportService reportService;
    private final String DELETED_COMMENT = "삭제된 댓글입니다.";

    public void saveVideoComment(VideoComment comment) {
        commentRepository.save(comment);
    }

    public VideoComment findParentComment(long parentId, Video video) {
        if (parentId == 0L) {
            return null;
        }
        return commentRepository.findByIdAndStatusAndVideo(parentId, true, video)
                .orElseThrow(WrongParentCommentException::new);
    }

    public List<VideoCommentResponse> getVideoCommentResponses(Video video, long loginId, long boardWriterId) {
        return findParentCommentsInVideo(video).stream()
                .map(comment -> toVideoCommentResponse(comment, boardWriterId, loginId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public VideoComment findVideoCommentById(long id, Video video) {
        return commentRepository.findByIdAndStatusAndVideo(id, true, video)
                .orElseThrow(NoSuchCommentException::new);
    }

    public void deleteVideoComment(VideoComment comment) {
        comment.setStatus(false);
        commentRepository.save(comment);
    }

    public boolean isCommentAlreadyReported(VideoComment comment, User user) {
        if (reportService.isAlreadyReported(user, comment)) {
            return true;
        }
        reportService.createNewReport(comment, user);
        return false;
    }

    public boolean isCommentDeletedByReport(VideoComment comment) {
        if (comment.isReportCountOverLimit()) {
            deleteVideoComment(comment);
            return true;
        }
        return false;
    }

    private List<VideoComment> findParentCommentsInVideo(Video video) {
        return video.getVideoComments()
                .stream()
                .filter(VideoComment::getIsParentComment)
                .collect(Collectors.toList());
    }

    private VideoCommentResponse toVideoCommentResponse(VideoComment comment, long boardWriterId, long loginId) {
        return setChildComments(VideoCommentResponse.fromEntity(comment, boardWriterId, loginId), comment,
                boardWriterId, loginId);
    }

    private VideoCommentResponse setChildComments(VideoCommentResponse response, VideoComment comment,
                                                  long boardWriterId, long loginId) {
        if (!comment.getIsParentComment()) {
            response.setNestedComments(null);
            return response;
        }
        List<VideoCommentResponse> childResponses = comment.getChildComments().stream()
                .filter(VideoComment::getStatus)
                .map(child -> VideoCommentResponse.fromEntity(child, boardWriterId, loginId))
                .collect(Collectors.toList());

        return filterDeletedParentComment(response, childResponses, !comment.getStatus());
    }

    private VideoCommentResponse filterDeletedParentComment(VideoCommentResponse response,
                                                            List<VideoCommentResponse> childs, boolean isDeleted) {
        if (isDeleted) {
            if (childs.isEmpty()) {
                return null;
            }
            response.setContent(DELETED_COMMENT);
        }
        response.setNestedComments(childs);
        return response;
    }

}
