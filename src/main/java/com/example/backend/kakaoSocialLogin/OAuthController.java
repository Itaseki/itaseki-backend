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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


        String id = kakaoId.toString();
        System.out.println(id);
        String nickname = userNickname.toString();
        nickname = nickname.substring(1,nickname.length()-1);
        String name = nickname;
        String profileUrl = userProfileUrl.toString();
        profileUrl = profileUrl.substring(1,profileUrl.length()-1);

        User user = userService.findUserByKakaoId(id);
        User newUser = new User();

        if(user == null){
            newUser.setKakaoId(id);
            newUser.setName(name);
            newUser.setNickname(nickname);
            newUser.setProfileUrl(profileUrl);
            newUser.setEmail("");
            newUser.setUserDescription("카카오톡 소셜로그인으로 사용자 추가");
            userService.saveUser(newUser);
        }

        String token = jwtAuthenticationProvider.createToken(newUser.getKakaoId(), newUser.getRoles());
        response.setHeader("ITASEKKI", token);
        Cookie cookie = new Cookie("ITASEKKI", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

//    @ResponseBody
//    @GetMapping("/test")
//    public void test(HttpServletRequest request) {
//        List<String> role = new ArrayList<>();
//        role.add("AUTHENTICATED");
//        String kakaoId = "12345678";
//        User user = new User();
//        user.setKakaoId(kakaoId);
//        user.setName("소셜 로그인 jwt 테스트");
//        user.setNickname("너무 피곤하다 ㅠ");
//        user.setProfileUrl("asdfasdf");
//        user.setUserDescription("asdf");
//        user.setEmail("dlrlxo999@naver.com");
//        user.setRoles(role);
//        userService.saveUser(user);
//        String token = jwtAuthenticationProvider.createToken(user.getKakaoId(), user.getRoles());
//        System.out.println(token);
//
//    }
//
//    @ResponseBody
//    @GetMapping("/test/token")
//    public void tokenTest(HttpServletRequest request){
//        String header = request.getHeader("ITASEKKI");
//        String kakaoId = jwtAuthenticationProvider.getUserPk(header);
//        User user = userService.findUserByKakaoId(kakaoId);
//        System.out.println(user.getKakaoId());
//        Authentication newAuth = new UsernamePasswordAuthenticationToken(kakaoId, null, user.getAuthorities());
//        System.out.println(newAuth);
//    }
//
//    @ResponseBody
//    @GetMapping("/test/token/principal")
//    public void principalTest(){
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    }

}