package com.example.backend.myPage;

import com.example.backend.blackList.domain.BlackList;
import com.example.backend.blackList.service.BlackListService;
import com.example.backend.main.dto.MainCommunityResponse;
import com.example.backend.utils.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user/{userId}")
@RequiredArgsConstructor
public class MyPageController {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final BlackListService blackListService;

    /**
     * 로그아웃
     * */
    @PatchMapping("/edit")
    public ResponseEntity<String> logout(HttpServletRequest request){
        String accessToken = jwtAuthenticationProvider.resolveToken(request);   // API를 요청한 토큰(Access Token) 가져오기
        BlackList blackList = new BlackList();
        blackList.setToken(accessToken);
        blackListService.saveBlackList(blackList);
        return new ResponseEntity<>("로그아웃 되었습니다.", HttpStatus.OK);
    }
}
