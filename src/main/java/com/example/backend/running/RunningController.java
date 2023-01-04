package com.example.backend.running;

import com.example.backend.reservation.ReservationService;
import com.example.backend.reservation.dto.NextRunResponse;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/running")
@RequiredArgsConstructor
public class RunningController {

    private final ReservationService reservationService;
    private final UserService userService;

    @GetMapping("")
    public HashMap<String, String> getRunningInformation(){
        HashMap<String, String> runningInformation = new HashMap<String, String>();
        Long loginId = 8L;
        User user = userService.findUserById(loginId);
        String userProfileUrl = user.getProfileUrl();
        NextRunResponse nextConfirm = reservationService.findNextConfirm();
        if(nextConfirm==null){
            return null;
        }
        else{
            String videoUrl = nextConfirm.getVideoUrl();
            runningInformation.put("videoUrl", videoUrl);
            runningInformation.put("userProfileUrl", userProfileUrl);
        }
        return runningInformation;
    }
}
