package com.example.backend.reservation;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.dto.*;
import com.example.backend.user.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        LocalDate date=LocalDate.parse(reservationDto.getReservationDate());
        Reservation reservation = Reservation.builder()
                .user(user).video(video)
                .sTime(reservationDto.getStartTime())
                .eTime(reservationDto.getEndTime())
                .date(date)
                .build();

        boolean endTimeValidate = reservationService.checkEndTimeValidate(reservation);
        if(!endTimeValidate){
            return new ResponseEntity<>("잘못된 예약 종료시간 입니다.",HttpStatus.BAD_REQUEST);
        }
        boolean existence= reservationService.findReservationByDateAndUser(date, user)!=null; //존재하면 true, 아니면 false

        boolean hasConfirmed=reservationService.findConfirmedReservation(date,video,reservationDto.getStartTime(),reservationDto.getEndTime())!=null; //존재하면 true, 아니면 flase
        if(hasConfirmed){
            return new ResponseEntity<>("이미 해당 시간에 예약이 확정되어 있는 영상",HttpStatus.OK);
        }

        if(existence){
            return new ResponseEntity<>("중복 예약 불가",HttpStatus.CONFLICT);
        }

        Boolean conflict = reservationService.checkReservationConflict(reservation);
        if(conflict){
            return new ResponseEntity<>("선택 불가능한 예약시간",HttpStatus.CONFLICT);
        }

        reservationService.saveReservation(reservation);
        return new ResponseEntity<>("예약 등록 성공",HttpStatus.CREATED);
    }


    @GetMapping("/title/search")
    public ResponseEntity<List<VideoTitleSearchResponse>> findVideoContainingTitle(@RequestParam String q){
        List<Video> videos = videoService.findVideoContainingTitle(q, "id");
        List<VideoTitleSearchResponse> searchResponses = videos.stream()
                .map(VideoTitleSearchResponse::fromEntity)
                .limit(5)
                .collect(Collectors.toList());
        return new ResponseEntity<>(searchResponses,HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<TimetableResponse>> getReservationTimetable(@RequestParam String start, @RequestParam String end,
                                                                           @RequestParam String select, @RequestParam String date){
//        System.out.println("start = " + start + ", end = " + end + ", select = " + select+", date = "+date);
        //시간 순 정렬
        return new ResponseEntity<>(reservationService.getTimeTable(start, end, select, date),HttpStatus.OK);
    }

    @GetMapping("/best")
    public ResponseEntity<List<BestReservationResponse>> getBest3Reservations(){
        //오늘 날짜 -> group by 해서 예약 수 기준으로 정렬 -> limit 3
        return new ResponseEntity<>(reservationService.getBestReservations(),HttpStatus.OK);
    }

    @GetMapping("/confirm")
    public ResponseEntity<List<ConfirmedReservationResponse>> getConfirmedReservation(@RequestParam String date){
        return new ResponseEntity<>(reservationService.findAllConfirmedReservationsByDate(LocalDate.parse(date)),HttpStatus.OK);
    }

    @GetMapping("/next")
    public ResponseEntity<NextRunResponse> getNextRunReservation(){
        //오늘 저녁 ~ 다음날 새벽 가능하던가?
        //이게 되면 if(todate(startTime) > toDate(endTime) -> (startTime의 date + 하루),endTime 을 toDate 로 변환!
        return new ResponseEntity<>(reservationService.findNextConfirm(),HttpStatus.OK);
    }
}
