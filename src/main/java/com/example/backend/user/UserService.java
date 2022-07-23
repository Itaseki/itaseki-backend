package com.example.backend.user;

import com.example.backend.report.Report;
import com.example.backend.user.domain.Subscribe;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;

    public User findUserById(Long userId){
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    public User findUserByKakaoId(String kakaoId){
        Optional<User> user = userRepository.findByKakaoId(kakaoId);
        return user.orElse(null);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String kakaoId) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByKakaoId(kakaoId);
        if (user.isPresent()){
            User loginUser = user.get();
            return new User(loginUser.getName(), loginUser.getNickname(), loginUser.getProfileUrl(), loginUser.getKakaoId());
        }
        return null;
    }

    public List<Subscribe> findAllSubscribingTargets(User user){
        return subscribeRepository.findAllByUser(user)
                .stream()
                .filter(Subscribe::getStatus)
                .collect(Collectors.toList());
    }

    public void saveSubscribe(User user, User target){
        Subscribe build = Subscribe.builder()
                .subscribeTarget(target)
                .lastModified(LocalDateTime.now())
                .user(user)
                .build();
        subscribeRepository.save(build);

    }

}
