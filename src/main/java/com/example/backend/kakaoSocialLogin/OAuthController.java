package com.example.backend.kakaoSocialLogin;

import com.example.backend.kakaoSocialLogin.service.OAuthService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public void kakaoCallback(@RequestParam String code) {
//        System.out.println(code);
//        oAuthService.getKakaoAccessToken(code); // Access Token 받기 (원래 프론트에서 줘야할 값, 테스트를 위해 만들어놈)
//        oAuthService.getKakaoUserInfo(code);
        JsonNode userInfo = oAuthService.getKakaoUserInfo(code);
        System.out.println(userInfo);
//        String email = userInfo.get("kaccount_email").toString();
//        String image = userInfo.get("properties").get("profile_image").toString();
//        String nickname = userInfo.get("properties").get("nickname").toString();
//        System.out.println(email);
//        System.out.println(image);
//        System.out.println(nickname);

    }

}
