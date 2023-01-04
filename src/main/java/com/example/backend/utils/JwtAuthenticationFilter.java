package com.example.backend.utils;

import com.example.backend.blackList.domain.BlackList;
import com.example.backend.blackList.service.BlackListService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final BlackListService blackListService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtAuthenticationProvider.resolveToken((HttpServletRequest) request);
        BlackList blackListEntity = blackListService.getBlackListEntity(token);

        if (token != null && jwtAuthenticationProvider.validateToken(token) && blackListEntity == null) {
            Authentication authentication = jwtAuthenticationProvider.authentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request,response);
    }
}