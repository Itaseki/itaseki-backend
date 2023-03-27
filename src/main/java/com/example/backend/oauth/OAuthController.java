package com.example.backend.oauth;

import com.example.backend.oauth.service.OAuthService;
import com.example.backend.user.domain.UserCounter;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.utils.JwtAuthenticationProvider;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth/")
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserService userService;
    private final UserCounter userCounter;

    /**
     * 카카오 callback
     * [POST] /oauth/kakao
     */

    @GetMapping("/kakao")
    public ResponseEntity<String> socialLogin(@RequestParam("accessCode") String accessCode) {

        JsonNode userInfo = oAuthService.getUserInfo(accessCode);

        JsonNode jsonKakaoId = userInfo.get("id");
        log.info(String.valueOf(jsonKakaoId));
//        JsonNode jsonUserNickname = userInfo.get("properties").get("profile_nickname");
//        log.info(String.valueOf(jsonUserNickname));
//        JsonNode jsonProfileUrl = userInfo.get("properties").get("profile_image");

//        String nickname = jsonUserNickname.toString();
//        nickname = nickname.substring(1,nickname.length()-1);
//        String profileUrl = jsonProfileUrl.toString();
//        profileUrl = profileUrl.substring(1,profileUrl.length()-1);
        String nickname = "테스트 계정 닉네임";
        Long kakaoId = Long.parseLong(jsonKakaoId.toString());

        User user = userService.findUserByKakaoId(kakaoId);

        if(user == null){
            User newUser = new User();
            JsonNode tempEmail = userInfo.get("kakao_account").get("email");
            if(tempEmail == null || tempEmail.isNull()){
                newUser.setEmail(null);
            }
            else{
                String email = tempEmail.toString();
                email = email.substring(1, email.length()-1);
                newUser.setEmail(email);
            }
            newUser.setKakaoId(kakaoId);
            newUser.setProfileUrl(null);
            newUser.setNickname(nickname);
            newUser.setUserDescription(null);
            userService.saveUser(newUser);
            user = newUser;
        }
        String jsonWebToken = jwtAuthenticationProvider.createJWT(user.getUserId());
        userCounter.loginNewUser(user.getUserId(), jsonWebToken);
        return new ResponseEntity<>(jsonWebToken, HttpStatus.OK);
    }
}
