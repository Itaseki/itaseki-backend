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

    public User findUserByEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public User findUserByKakaoId(Long id){
        Optional<User> user = userRepository.findByKakaoId(id);
        return user.orElse(null);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
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
