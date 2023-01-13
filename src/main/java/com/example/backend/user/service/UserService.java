package com.example.backend.user.service;

import com.example.backend.user.domain.Subscribe;
import com.example.backend.user.domain.User;
import com.example.backend.user.exception.NoSuchUserException;
import com.example.backend.user.exception.WrongAuthorizationException;
import com.example.backend.user.repository.SubscribeRepository;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void saveUser(User user){
        userRepository.save(user);
    }

    public void checkUserAuthority(Long loginId, Long requestTargetId) {
        if (!loginId.equals(requestTargetId)) {
            throw new WrongAuthorizationException();
        }
    }

    public User findExistingUser(Long userId) {
        User user = findUserById(userId);
        if (user == null || !user.isUserExist()) {
            throw new NoSuchUserException();
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(Long.parseLong(username));
        if (user.isPresent()){
            return user.get();
        }
        else{
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }

    public List<Subscribe> findAllSubscribingTargets(User user){
        return subscribeRepository.findAllByUser(user)
                .stream()
                .filter(Subscribe::getStatus)
                .collect(Collectors.toList());
    }

}
