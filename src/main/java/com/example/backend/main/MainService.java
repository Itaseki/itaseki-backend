package com.example.backend.main;

import com.example.backend.main.dto.MainNextRunResponse;
import com.example.backend.main.dto.MainPlaylistResponse;
import com.example.backend.main.dto.MainUserResponse;
import com.example.backend.main.dto.MainVideo;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import com.example.backend.playlist.repository.PlaylistRepository;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.reservation.ReservationService;
import com.example.backend.reservation.dto.NextRunResponse;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.video.repository.VideoRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {
    private final UserService userService;
    private final VideoRepository videoRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistService playlistService;
    private final ReservationService reservationService;
    private final int VIDEO_COUNT = 5;
    private final int PLAYLIST_COUNT = 1;

    public List<MainVideo> getVideoForMain() {
        return videoRepository.findBestVideos(VIDEO_COUNT)
                .stream()
                .map(MainVideo::ofVideo)
                .collect(Collectors.toList());
    }

    public MainPlaylistResponse getPlaylistsForMain() {
        List<AllPlaylistsResponse> playlists = playlistRepository.findBestPlaylists(PLAYLIST_COUNT);
        if (playlists.isEmpty()) {
            return null;
        }
        return MainPlaylistResponse.builder()
                .playlist(playlists.get(0))
                .titleImage(playlistService.getFirstThumbnailInPlaylist(playlists.get(0).getId()))
                .videos(playlistService.findAllVideosInPlaylist(playlists.get(0).getId()))
                .build();
    }


    public MainUserResponse getUserProfileForMain(Long loginId) {
        User user = userService.findUserById(loginId);
        return MainUserResponse.fromEntity(user);
    }

    public MainNextRunResponse getTodaysNextConfirm() {
        NextRunResponse nextConfirm = reservationService.findNextConfirm();
        if (nextConfirm == null) {
            return null;
        }
        if (!LocalDate.parse(nextConfirm.getReservationDate()).equals(LocalDate.now())) {
            return null;
        }
        return MainNextRunResponse.ofReservation(nextConfirm);
    }

}
