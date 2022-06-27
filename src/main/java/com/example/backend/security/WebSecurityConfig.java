package com.example.backend.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //토큰 인증이 불가능해도 졉근 가능해야 하는 url 들 (보통 로그인, 회원가입 등)
    private static final String[] PERMIT_URL_ARRAY = {
            "/","/**/*.png","/**/*.jpg","/**/*.js","/**/*.css","/**/*.html","/**/*.gif","/**/*.svg"
            ,"/signup","/signup/**","/signin","/signin/**"//소셜 로그인 관련한 부분도 추가
            ,"/test","/test-db","/test-db/**" //s3, rds 연동 테스트때 사용한 url 들 일단 접근 허가
            ,"/boards/**"
            ,"/run/reservations","/run/reservations/**" //로그인 구현 전 영상 달리기 예약 위해 임시허가
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .cors().and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.OPTIONS,"/**").permitAll() //Preflight 용 설정
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .antMatchers(HttpMethod.GET).permitAll()//일단은 모든 GET 요청은 토큰 없어도 접근 허가되도록 써놨어유 나중에 개발하면서 추가 변경!
                .anyRequest().authenticated(); //위에 작성한 url 들을 제외하고는 다 인증 필요 (기타 post, patch delete 등은 다 인증된 사용자만 접근 가능)

    }
}
