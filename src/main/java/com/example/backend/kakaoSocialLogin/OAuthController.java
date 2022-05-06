package com.example.backend.kakaoSocialLogin;

import com.example.backend.kakaoSocialLogin.service.OAuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.shaded.json.JSONObject;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 카카오 callback
     * [GET] /oauth/kakao
     */
    @ResponseBody
    @GetMapping("/kakao")
    public String kakaoCallback(@RequestParam String code) {

        JsonNode userInfo = oAuthService.getKakaoUserInfo(code);
        // 카카오에서 받아온 정보들 중에 유일한 값 "id" 파싱하기
        JsonNode userId = userInfo.get("id");
        // 이 id를 DB를 돌면서 있는지 없는지 check 후 없으며 넣어주기
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


}
