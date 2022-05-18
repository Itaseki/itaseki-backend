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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
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
        User newUser = new User();

        if(user == null){
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setNickname(nickname);
            newUser.setProfileUrl(profileUrl);
            newUser.setUserDescription("");
            userService.saveUser(newUser);
        }

        String token = jwtAuthenticationProvider.createToken(newUser.getEmail(), newUser.getRoles());
        response.setHeader("X-AUTH-TOKEN", token);

        Cookie cookie = new Cookie("X-AUTH-TOKEN", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
//        System.out.println(token);
    }

}