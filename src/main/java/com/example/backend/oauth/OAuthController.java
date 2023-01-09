package com.example.backend.oauth;

import com.example.backend.oauth.dto.OauthDto;
import com.example.backend.oauth.service.OAuthService;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.utils.JwtAuthenticationProvider;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth/")
public class OAuthController {

    private final OAuthService oAuthService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserService userService;

    /**
     * 카카오 callback
     * [POST] /oauth/kakao
     */

    @PostMapping("/kakao")
    public ResponseEntity<String> socialLogin(OauthDto oauthDto) {

        JsonNode userInfo = oAuthService.getUserInfo(oauthDto.getAccessCode());

        JsonNode jsonUserNickname = userInfo.get("properties").get("nickname");
        JsonNode jsonProfileUrl = userInfo.get("properties").get("profile_image");
        JsonNode jsonEmail = userInfo.get("kakao_account").get("email");

        String nickname = jsonUserNickname.toString();
        nickname = nickname.substring(1,nickname.length()-1);
        String profileUrl = jsonProfileUrl.toString();
        profileUrl = profileUrl.substring(1,profileUrl.length()-1);
        String email = jsonEmail.toString();
        email = email.substring(1,email.length()-1);

        User user = userService.findUserByEmail(email);

        if(user == null){
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setProfileUrl(profileUrl);
            newUser.setNickname(nickname);
            newUser.setUserDescription(null);
            userService.saveUser(newUser);
            user = newUser;
        }
        String jsonWebToken = jwtAuthenticationProvider.createJWT(user.getUserId());
        return new ResponseEntity<>(jsonWebToken, HttpStatus.OK);
    }
}