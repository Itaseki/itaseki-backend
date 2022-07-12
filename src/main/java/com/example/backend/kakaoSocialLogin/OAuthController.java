package com.example.backend.kakaoSocialLogin;

//import com.example.backend.kakaoSocialLogin.service.JwtTokenProviderService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth/")
public class OAuthController {

    private final OAuthService oAuthService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserService userService;

    /**
     * 카카오 callback
     * [GET] /oauth/kakao
     */
    @ResponseBody
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) {

        JsonNode userInfo = oAuthService.getKakaoUserInfo(code);

        JsonNode kakaoId = userInfo.get("id");
        JsonNode userNickname = userInfo.get("properties").get("nickname");
        JsonNode userProfileUrl = userInfo.get("properties").get("profile_image");


        Long id = kakaoId.asLong();
        System.out.println(id);
        String nickname = userNickname.toString();
        nickname = nickname.substring(1,nickname.length()-1);
        String name = nickname;
        String profileUrl = userProfileUrl.toString();
        profileUrl = profileUrl.substring(1,profileUrl.length()-1);

        User user = userService.findUserById(id);
        User newUser = new User();

        if(user == null){
            newUser.setUserId(id);
            newUser.setName(name);
            newUser.setNickname(nickname);
            newUser.setProfileUrl(profileUrl);
            newUser.setUserDescription("");
            userService.saveUser(newUser);
        }


        String token = jwtAuthenticationProvider.createToken(newUser.getUserId(), newUser.getRoles());
        response.setHeader("ITASEKKI", token);
        Cookie cookie = new Cookie("ITASEKKI", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

}