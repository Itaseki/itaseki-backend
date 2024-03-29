package com.example.backend.myPage;

import com.example.backend.blackList.domain.BlackList;
import com.example.backend.blackList.service.BlackListService;
import com.example.backend.myPage.dto.MyPageCommentPageableResponse;
import com.example.backend.myPage.dto.MyPagePageableResponse;
import com.example.backend.myPage.dto.UserInfoResponse;
import com.example.backend.myPage.dto.NicknameEditRequest;
import com.example.backend.user.domain.UserCounter;
import com.example.backend.user.domain.User;
import com.example.backend.user.service.UserService;
import com.example.backend.utils.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user/{userId}")
@RequiredArgsConstructor
public class MyPageController {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final BlackListService blackListService;
    private final UserService userService;
    private final MyPageService myPageService;
    private final UserCounter userCounter;

    /**
     * 로그아웃
     */
    @PatchMapping(value = "/edit", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = jwtAuthenticationProvider.resolveToken(request);   // API를 요청한 토큰(Access Token) 가져오기
        BlackList blackList = new BlackList();
        blackList.setToken(accessToken);
        blackListService.saveBlackList(blackList);
        userCounter.logoutUser(accessToken);
        return new ResponseEntity<>("로그아웃 되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getUserInformation(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.findUserBasicInformation(findUserAndCheckAuthority(userId)),
                HttpStatus.OK);
    }

    @GetMapping("/playlists")
    public ResponseEntity<MyPagePageableResponse> getMyPagePlaylist(@PathVariable Long userId,
                                                                    @RequestParam(required = false, defaultValue = "my") String type,
                                                                    @PageableDefault(size = 8) Pageable pageable) {
        return new ResponseEntity<>(myPageService.findPlaylistsForMyPage(findUserAndCheckAuthority(userId), type, pageable),
                HttpStatus.OK);
    }

    @GetMapping("/videos")
    public ResponseEntity<MyPagePageableResponse> getMyPageVideo(@PathVariable Long userId,
                                                                 @PageableDefault(size = 8) Pageable pageable) {
        return new ResponseEntity<>(myPageService.findVideosForMyPage(findUserAndCheckAuthority(userId), pageable),
                HttpStatus.OK);
    }

    @GetMapping("/comments")
    public ResponseEntity<MyPageCommentPageableResponse> getMyPageComment(@PathVariable Long userId,
                                                                          @PageableDefault(size = 10) Pageable pageable) {
        return new ResponseEntity<>(myPageService.findCommentsForMyPage(findUserAndCheckAuthority(userId), pageable),
                HttpStatus.OK);
    }

    @PatchMapping("/info/image")
    public ResponseEntity<String> updateProfileInfo(@PathVariable Long userId, MultipartFile file) {
        return new ResponseEntity<>(myPageService.updateProfileImage(findUserAndCheckAuthority(userId), file),
                HttpStatus.OK);
    }

    @PatchMapping(value = "/info/nickname", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> updateNickname(@PathVariable Long userId,
                                                 @RequestBody NicknameEditRequest nicknameEditRequest) {
        return new ResponseEntity<>(myPageService.updateNickname(findUserAndCheckAuthority(userId),
                nicknameEditRequest.getNickname()),
                HttpStatus.OK);
    }

    @DeleteMapping(value = "/edit", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.deleteUser(findUserAndCheckAuthority(userId)), HttpStatus.NO_CONTENT);
    }

    private User findUserByAuthentication() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findUserById(Long.parseLong(principal.getUsername()));
    }

    private User findUserAndCheckAuthority(Long userId) {
        User user = userService.findExistingUser(userId);
        userService.checkUserAuthority(findUserByAuthentication().getUserId(), userId);
        return user;
    }
}
