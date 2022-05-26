package com.example.backend.reservation;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.dto.ReservationDto;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/run/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final VideoService videoService;
    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<String> registerVideoReservation(@RequestBody ReservationDto reservationDto){
        Long loginId=1L;
        User user = userService.findUserById(loginId);
        Video video = videoService.findVideoEntityById(reservationDto.getId());
        if(video==null)
            return new ResponseEntity<>("잘못된 영상에 대한 예약 요청", HttpStatus.NOT_FOUND);
        Reservation reservation = Reservation.builder()
                .user(user).video(video)
                .sTime(reservationDto.getStartTime())
                .eTime(reservationDto.getEndTime())
                .date(LocalDate.parse(reservationDto.getReservationDate()))
                .build();
        Reservation saveReservation = reservationService.saveReservation(reservation);
        if(saveReservation!=null)
            return new ResponseEntity<>("예약 등록 성공",HttpStatus.CREATED);
        return new ResponseEntity<>("선택 불가능한 예약시간",HttpStatus.CONFLICT);
    }

    @PostMapping("/test")
    public void saveConfirm(@RequestBody ReservationDto reservationDto){
        Video video = videoService.findVideoEntityById(reservationDto.getId());
        ConfirmedReservation build = ConfirmedReservation.builder()
                .video(video)
                .startTime(reservationDto.getStartTime())
                .endTime(reservationDto.getEndTime())
                .date(LocalDate.parse(reservationDto.getReservationDate()))
                .build();

        reservationService.saveConfirm(build);
    }
}
