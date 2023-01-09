package com.example.backend.reservation;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.dto.*;
import com.example.backend.reservation.exception.ConfirmExistException;
import com.example.backend.reservation.exception.DuplicateReservationException;
import com.example.backend.reservation.exception.ReservationTimeConflictException;
import com.example.backend.reservation.exception.WrongEndTimeException;
import com.example.backend.user.service.UserService;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Video;
import com.example.backend.video.service.VideoService;
import java.time.format.DateTimeParseException;
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
    public ResponseEntity<String> registerVideoReservation(@RequestBody ReservationDto reservationDto) {
        Long loginId = 1L;
        User user = userService.findUserById(loginId);
        Video video = videoService.findVideoEntityById(reservationDto.getVideoId());
        if (video == null) {
            return new ResponseEntity<>("잘못된 영상에 대한 예약 요청", HttpStatus.NOT_FOUND);
        }

        reservationService.saveReservation(reservationDto, user, video);
        return new ResponseEntity<>("예약 등록 성공", HttpStatus.CREATED);
    }


    @GetMapping("/title/search")
    public ResponseEntity<List<VideoTitleSearchResponse>> findVideoContainingTitle(@RequestParam String query) {
        List<Video> videos = videoService.findVideoContainingTitle(query, "id");
        List<VideoTitleSearchResponse> searchResponses = videos.stream()
                .map(VideoTitleSearchResponse::fromEntity)
                .limit(5)
                .collect(Collectors.toList());
        return new ResponseEntity<>(searchResponses, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<TimetableResponse>> getReservationTimetable(@RequestParam String start,
                                                                           @RequestParam String end,
                                                                           @RequestParam String select,
                                                                           @RequestParam String date) {
        return new ResponseEntity<>(reservationService.getTimeTable(start, end, select, date), HttpStatus.OK);
    }

    @GetMapping("/best")
    public ResponseEntity<List<BestReservationResponse>> getBest3Reservations() {
        return new ResponseEntity<>(reservationService.getBestReservations(), HttpStatus.OK);
    }

    @GetMapping("/confirm")
    public ResponseEntity<List<ConfirmedReservationResponse>> getConfirmedReservation(@RequestParam String date) {
        return new ResponseEntity<>(reservationService.findAllConfirmedReservationsByDate(LocalDate.parse(date)),
                HttpStatus.OK);
    }

    @GetMapping("/next")
    public ResponseEntity<NextRunResponse> getNextRunReservation() {
        return new ResponseEntity<>(reservationService.findNextConfirm(), HttpStatus.OK);
    }

    @ExceptionHandler(DateTimeParseException.class)
    ResponseEntity<String> handleWrongDateFormat(DateTimeParseException exception) {
        return new ResponseEntity<>("날짜 및 숫자 입력 포맷이 잘못되었습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateReservationException.class)
    ResponseEntity<String> handleDuplicateReservationRequest(DuplicateReservationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(WrongEndTimeException.class)
    ResponseEntity<String> handleEndTimeOverNextDay(WrongEndTimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConfirmExistException.class)
    ResponseEntity<String> handleConfirmedReservationExistence(ConfirmExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.OK);
    }

    @ExceptionHandler(ReservationTimeConflictException.class)
    ResponseEntity<String> handleRequestTimeConflict(ReservationTimeConflictException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<DetailReservationResponse> getReservationDetail(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.findReservationById(reservationId);
        return new ResponseEntity<>(DetailReservationResponse.fromReservation(reservation,
                reservationService.getReservationsCount(reservation)), HttpStatus.OK);
    }

    @GetMapping("/confirm/{confirmedReservationId}")
    public ResponseEntity<DetailReservationResponse> getConfirmedReservationDetail(
            @PathVariable Long confirmedReservationId) {
        ConfirmedReservation reservation = reservationService.findConfirmById(confirmedReservationId);
        return new ResponseEntity<>(DetailReservationResponse.fromConfirm(reservation,
                reservationService.getReservationsCount(reservation)), HttpStatus.OK);
    }
}
