package com.example.backend.myPage;

import com.example.backend.myPage.dto.UserInfoDto;
import com.example.backend.playlist.domain.Playlist;
import com.example.backend.playlist.service.PlaylistService;
import com.example.backend.user.domain.Subscribe;
import com.example.backend.user.domain.User;
import com.example.backend.user.repository.SubscribeRepository;
import com.example.backend.user.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserService userService;
    private final PlaylistService playlistService;
    private final SubscribeRepository subscribeRepository;

    public UserInfoDto findUserBasicInformation(User user) {
        return UserInfoDto.fromUserAndDetail(user,
                findAllPublicPlaylistByUser(user).size(),
                findMySubscribers(user).size(),
                getUserReportedCount(user));
    }

    private List<Playlist> findAllPublicPlaylistByUser(User user) {
        return playlistService.findAllPublicPlaylistsByUserDesc(user);
    }

    private List<User> findMySubscribers(User user) {
        return subscribeRepository.findAllBySubscribeTarget(user)
                .stream()
                .filter(Subscribe::getStatus)
                .map(Subscribe::getUser)
                .collect(Collectors.toList());
    }

    private int getUserReportedCount(User user) {
        return user.getUserReportCount();
    }
}
