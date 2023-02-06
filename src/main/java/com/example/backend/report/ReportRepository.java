package com.example.backend.report;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.domain.PlaylistComment;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.domain.VideoComment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByUserAndCommunityBoard(User user, CommunityBoard communityBoard);
    Report findByUserAndCommunityComment(User user, CommunityComment comment);
    Report findByUserAndImageBoard(User user, ImageBoard imageBoard);
    Optional<Report> findByUserAndVideo(User user, Video video);
    Optional<Report> findByUserAndVideoComment(User user, VideoComment comment);
    Report findByUserAndPlaylist(User user, Playlist playlist);
    Report findByUserAndPlaylistComment(User user, PlaylistComment comment);
}
