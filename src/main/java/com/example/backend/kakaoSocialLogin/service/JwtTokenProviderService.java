package com.example.backend.kakaoSocialLogin.service;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.time.Duration;
import java.util.Date;

@Service
public class JwtTokenProviderService {

    public String createToken(String userId){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("fresh")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis()))
                .claim("id", userId)
                .signWith(SignatureAlgorithm.HS256, "secret")
                .compact();
    }

//    public String createRefreshToken(){
//        Date now = new Date();
//        return Jwts.builder()
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond))
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }

}
