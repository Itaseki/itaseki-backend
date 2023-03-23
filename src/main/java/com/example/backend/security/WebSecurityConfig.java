package com.example.backend.security;

import com.example.backend.blackList.service.BlackListService;
import com.example.backend.utils.JwtAuthenticationFilter;
import com.example.backend.utils.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final BlackListService blackListService;

    /**
     * 토큰이 필요한 url 먼저 검사한다.
     * */

    //토큰 인증이 불가능해도 졉근 가능해야 하는 url 들 (보통 로그인, 회원가입 등)
    private static final String[] PERMIT_URL_ARRAY = {
            "/","/**/*.png","/**/*.jpg","/**/*.js","/**/*.css","/**/*.html","/**/*.gif","/**/*.svg"
            ,"/main", "/main/**"
            ,"/search", "/search/**"
            ,"/oauth", "/oauth/**"  // 카카오톡 소셜로그인
            ,"/boards", "/boards/**"
            ,"/run", "/run/**"
    };

    private static final String[] AUTHENTICATED_URL_ARRAY={
            "/user", "/user/**"
            ,"/main/user"
            ,"/boards/video/**/likes", "/boards/video/**/reports"
            ,"/boards/video/{videoId}", "/boards/video/info/{userId}"
            ,"/boards/video/{videoId}/comments", "/boards/video/{videoId}/comments/{videoCommentId}"
            ,"/boards/video/{videoId}/comments/{videoCommentId}/reports"
            ,"/boards/video"
            ,"/run/reservations"
            ,"/boards/playlist/user/{userId}", "/boards/playlist/saved", "/boards/playlist"
            ,"/boards/playlist/{playlistId}", "/boards/playlist/{playlistId}", "/boards/playlist/saved"
            ,"/boards/playlist/subscribe", "/boards/playlist/{playlistId}/likes", "/boards/playlist/{playlistId}/reports"
            ,"/boards/playlist/{playlistId}", "/boards/playlist/{playlistId}/comments", "/boards/playlist/{playlistId}/comments/{commentId}"
            ,"/boards/playlist/{playlistId}/comments/{commentId}"
            ,"/chat/**", "/chat"

    };

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .cors().and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll();

        http.httpBasic().disable()
                .authorizeRequests()// 요청에 대한 사용권한 체크
                .antMatchers(HttpMethod.POST).authenticated()
                .antMatchers(HttpMethod.GET, "/boards/video/info/{userId}").authenticated() // authenticated 필터링이 안되길래 임시 추가
                .antMatchers(HttpMethod.GET).permitAll()
                .antMatchers(AUTHENTICATED_URL_ARRAY).authenticated()
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .anyRequest().denyAll()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtAuthenticationProvider, blackListService),
                        UsernamePasswordAuthenticationFilter.class); // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
        // + 토큰에 저장된 유저정보를 활용하여야 하기 때문에 CustomUserDetailService 클래스를 생성합니다.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
