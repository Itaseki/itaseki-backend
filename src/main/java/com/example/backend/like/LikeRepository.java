package com.example.backend.like;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.image.domain.ImageBoard;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    Like findByUserAndCommunityBoard(User user, CommunityBoard communityBoard);
    List<Like> findAllByCommunityBoard(CommunityBoard communityBoard);
    Like findByUserAndImageBoard(User user, ImageBoard imageBoard);
    List<Like> findAllByImageBoard(ImageBoard imageBoard);
    Optional<Like> findByUserAndVideo(User user, Video video);
    Optional<Like> findByUserAndPlaylist(User user, Playlist playlist);
    List<Like> findAllByUser(User user); // 사용자가 좋아요한 모든 데이터 조회
}
