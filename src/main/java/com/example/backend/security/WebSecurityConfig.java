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

    //토큰 인증이 불가능해도 졉근 가능해야 하는 url 들 (보통 로그인, 회원가입 등)
    private static final String[] PERMIT_URL_ARRAY = {
            "/","/**/*.png","/**/*.jpg","/**/*.js","/**/*.css","/**/*.html","/**/*.gif","/**/*.svg"
            ,"/signup","/signup/**","/signin","/signin/**"//소셜 로그인 관련한 부분도 추가
            ,"/test","/test-db","/test-db/**" //s3, rds 연동 테스트때 사용한 url 들 일단 접근 허가
            ,"/boards/**"
            ,"/run/reservations","/run/reservations/**" //로그인 구현 전 영상 달리기 예약 위해 임시허가
            ,"/boards/image", "/boards/image/**"
            ,"/chat/**", "/chat"
            ,"/oauth", "/oauth/**"
            ,"/user", "/user/**"    // 마이페이지 관련 부분 (임시허가)
            ,"/main", "/main/**"
            ,"/search", "/search/**"
    };

    private static final String[] AUTHENTICATED_URL_ARRAY={
            "/jwt"  // Spring Security 테스트 (나중에는 Permit Url 여기로 옮기기)
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
