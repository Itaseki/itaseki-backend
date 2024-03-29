package com.example.backend.user.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Setter
@Table(name = "user")
public class User implements UserDetails{

    /**
     * User Database에 필요한 것들
     * userId
     * nickname
     * email
     * profileUrl
     * userReportCount
     */

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Long kakaoId;

    @Column(nullable = true)
    private String profileUrl;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String userDescription;

    @Column(nullable = false)
    private Integer userReportCount=0;

    @Column(name = "user_status")
    private boolean status = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void updateUserReportCount() {
        this.userReportCount += 1;
    }

    public static User createAnonymousUser() {
        User user = new User();
        user.setUserId(0L);
        user.setNickname("anonymousUser");
        return user;
    }

    public String updateProfileImage(String url) {
        this.profileUrl = url;
        return this.profileUrl;
    }

    public String updateNickname(String nickname) {
        this.nickname = nickname;
        return this.nickname;
    }

    public void deleteUser() {
        this.nickname = "알 수 없음";
        this.status = false;
    }

    public boolean isUserExist() {
        return this.status;
    }
}


