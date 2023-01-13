package com.example.backend.myPage;

import com.example.backend.blackList.domain.BlackList;
import com.example.backend.blackList.service.BlackListService;
import com.example.backend.myPage.dto.DetailPlaylistDto;
import com.example.backend.myPage.dto.LikeDataDto;
import com.example.backend.myPage.dto.MyDataDto;
import com.example.backend.myPage.dto.MyPagePlaylistDto;
import com.example.backend.myPage.dto.MySubscribeDto;
import com.example.backend.myPage.dto.SubscribeRequest;
import com.example.backend.myPage.dto.UserEditInfoDto;
import com.example.backend.myPage.dto.UserEditRequest;
import com.example.backend.myPage.dto.UserInfoDto;
import com.example.backend.playlist.exception.PlaylistNotFoundException;
import com.example.backend.user.domain.User;
import com.example.backend.user.service.UserService;
import com.example.backend.utils.JwtAuthenticationProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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

    /**
     * 로그아웃
     */
    @PatchMapping("/edit")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = jwtAuthenticationProvider.resolveToken(request);   // API를 요청한 토큰(Access Token) 가져오기
        BlackList blackList = new BlackList();
        blackList.setToken(accessToken);
        blackListService.saveBlackList(blackList);
        return new ResponseEntity<>("로그아웃 되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDto> getHeaderUserInformation(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.findUserBasicInformation(findUserAndCheckAuthority(userId)),
                HttpStatus.OK);
    }

    @GetMapping("/likes")
    public ResponseEntity<LikeDataDto> getMyPageLikeData(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.getAllLikedData(findUserAndCheckAuthority(userId)), HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<MyDataDto> getMyPageUploadedDataByUser(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.getAllDataUploadedByUser(findUserAndCheckAuthority(userId)),
                HttpStatus.OK);
    }

    @GetMapping("/my/playlist")
    public ResponseEntity<List<MyPagePlaylistDto>> getMyUploadedPlaylist(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.findAllPlaylistByUser(findUserAndCheckAuthority(userId)),
                HttpStatus.OK);
    }

    @GetMapping("/my/saved/playlist")
    public ResponseEntity<List<MyPagePlaylistDto>> getSavedPlaylist(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.findAllSavedPlaylist(findUserAndCheckAuthority(userId)),
                HttpStatus.OK);
    }

    @GetMapping("/my/playlist/{playlistId}")
    public ResponseEntity<DetailPlaylistDto> getDetailMyPagePlaylist(@PathVariable Long userId,
                                                                     @PathVariable Long playlistId) {
        findUserAndCheckAuthority(userId);
        return new ResponseEntity<>(myPageService.getMyPagePlaylistDetail(playlistId), HttpStatus.OK);
    }

    @GetMapping("/subscribe")
    public ResponseEntity<MySubscribeDto> getSubscribeInformation(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.getMyPageSubscribeInfo(findUserAndCheckAuthority(userId)),
                HttpStatus.OK);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeUser(@PathVariable Long userId, @RequestBody SubscribeRequest request) {
        User user = findUserAndCheckAuthority(userId);
        User target = userService.findExistingUser(request.getTargetId());
        myPageService.saveSubscribe(user, target);
        return new ResponseEntity<>("구독 정보 업데이트 성공", HttpStatus.OK);
    }

    @GetMapping("/edit")
    public ResponseEntity<UserEditInfoDto> getUserInfoForEdit(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.getUserInfoForEdit(findUserAndCheckAuthority(userId)),
                HttpStatus.OK);
    }

    @PostMapping(value = "/edit", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> editUserProfile(@RequestPart UserEditRequest editRequest,
                                                  @RequestPart(required = false) MultipartFile profileImage,
                                                  @PathVariable Long userId) {
        myPageService.editUserInfo(findUserAndCheckAuthority(userId), editRequest, profileImage);
        return new ResponseEntity<>("프로필 정보 업데이트 성공", HttpStatus.CREATED);
    }

    @DeleteMapping("/edit")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return new ResponseEntity<>(myPageService.deleteUser(findUserAndCheckAuthority(userId)), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(PlaylistNotFoundException.class)
    public ResponseEntity<String> handlePlaylistNotFoundException(PlaylistNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    private User findUserByAuthentication() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findUserById(Long.parseLong(principal.getUsername()));
    }

    private User findUserAndCheckAuthority(Long userId) {
        User user = userService.findExistingUser(userId); // 존재하지 않는 user 에 대한 마이페이지 요청이면 예외 발생
        userService.checkUserAuthority(findUserByAuthentication().getUserId(), userId); // 로그인한 사용자와 요청한 유저가 다르다면 예외 발생
        return user;
    }
}
