package com.example.backend.kakaoSocialLogin;

import com.example.backend.kakaoSocialLogin.service.JwtTokenProviderService;
import com.example.backend.kakaoSocialLogin.service.OAuthService;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final JwtTokenProviderService jwtTokenProviderService;
    private final UserService userService;

    /**
     * 카카오 callback
     * [GET] /oauth/kakao
     */
    @ResponseBody
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code) {

        JsonNode userInfo = oAuthService.getKakaoUserInfo(code);

        JsonNode userEmail = userInfo.get("kakao_account").get("email");
        JsonNode userNickname = userInfo.get("properties").get("nickname");
        JsonNode userProfileUrl = userInfo.get("properties").get("profile_image");

        String email = userEmail.toString();
        email = email.substring(1,email.length()-1);
        String nickname = userNickname.toString();
        nickname = nickname.substring(1,nickname.length()-1);
        String name = nickname;
        String profileUrl = userProfileUrl.toString();
        profileUrl = profileUrl.substring(1,profileUrl.length()-1);

        User user = userService.findUserByEmail(email);

        if(user == null){
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setNickname(nickname);
            newUser.setProfileUrl(profileUrl);
            newUser.setUserDescription("");
            userService.saveUser(newUser);
        }

        // 로그인 처리

        // 스프링 시큐리티 통해 인증된 사용자로 등록

    }

}