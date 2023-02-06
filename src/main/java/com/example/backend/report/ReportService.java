package com.example.backend.report;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.user.domain.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public void saveReport(Report report, User reportedUser) {
        reportRepository.save(report);
        reportedUser.updateUserReportCount();
        userRepository.save(reportedUser);
    }

    public Boolean checkReportExistence(User user, CommunityBoard communityBoard) {
        Report report = reportRepository.findByUserAndCommunityBoard(user, communityBoard);
        return report != null;
    }

    public Boolean checkReportExistence(User user, ImageBoard imageBoard) {
        Report report = reportRepository.findByUserAndImageBoard(user, imageBoard);
        return report != null;
    }

    public Boolean checkReportExistence(User user, CommunityComment comment) {
        Report report = reportRepository.findByUserAndCommunityComment(user, comment);
        return report != null;
    }

    public boolean isAlreadyReported(User user, Video video) {
        return reportRepository.findByUserAndVideo(user, video).isPresent();
    }

    public boolean isAlreadyReported(User user, VideoComment comment) {
        return reportRepository.findByUserAndVideoComment(user, comment).isPresent();
    }

    public Boolean checkReportExistence(User user, Playlist playlist) {
        return reportRepository.findByUserAndPlaylist(user, playlist) != null;
    }

    public Boolean checkReportExistence(User user, PlaylistComment comment) {
        return reportRepository.findByUserAndPlaylistComment(user, comment) != null;
    }

    public void createNewReport(Video video, User user) {
        saveReport(Report.builder()
                .user(user)
                .video(video)
                .build(), video.getUser());
    }

    public void createNewReport(VideoComment comment, User user) {
        saveReport(Report.builder()
                .user(user)
                .videoComment(comment)
                .build(), comment.getUser());
    }

}
