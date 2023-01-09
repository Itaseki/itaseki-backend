package com.example.backend.reservation;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.dto.*;
import com.example.backend.reservation.exception.ConfirmExistException;
import com.example.backend.reservation.exception.DuplicateReservationException;
import com.example.backend.reservation.exception.ReservationTimeConflictException;
import com.example.backend.reservation.exception.WrongEndTimeException;
import com.example.backend.reservation.repository.ConfirmedReservationRepository;
import com.example.backend.reservation.repository.ReservationRepository;
import com.example.backend.video.domain.Video;
import com.example.backend.user.domain.User;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ConfirmedReservationRepository confirmedRepository;
    private final long CONFIRM_CRITERIA = 2L;

    public void saveReservation(ReservationDto reservationDto, User user, Video video) {
        if (isOverNextDay(reservationDto.getStartTime(), reservationDto.getEndTime())) {
            throw new WrongEndTimeException();
        }

        if (isAlreadySameConfirmedReservationExist(video, reservationDto.getStartTime(), reservationDto.getEndTime())) {
            throw new ConfirmExistException();
        }

        if (isUserReservationDuplicate(reservationDto.getReservationDate(), user)) {
            throw new DuplicateReservationException();
        }

        if (!isAbleToReserve(reservationDto.getStartTime(), reservationDto.getEndTime())) {
            throw new ReservationTimeConflictException();
        }

        reservationRepository.save(Reservation.fromDtoAndUserVideo(reservationDto, user, video));
        // 예약 업데이트
    }

    public void saveConfirm(ConfirmedReservation confirm) {
        confirmedRepository.save(confirm);
    }

    private boolean isAbleToReserve(LocalDateTime startTime, LocalDateTime endTime) {
        return confirmedRepository.findAllByReservationDate(startTime.toLocalDate())
                .stream()
                .allMatch(reservation -> isPossibleReservationTime(startTime, endTime, reservation));
    }

    private boolean isOverNextDay(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime.isAfter(endTime);
    }

    private boolean isUserReservationDuplicate(LocalDate reservationDate, User user) {
        return reservationRepository.findByReservationDateAndUser(reservationDate, user)
                .isPresent();
    }

    private boolean isAlreadySameConfirmedReservationExist(Video video, LocalDateTime startTime,
                                                           LocalDateTime endTime) {
        return confirmedRepository.findByStartTimeAndEndTimeAndVideo(startTime, endTime, video)
                .isPresent();
    }

    private boolean isPossibleReservationTime(LocalDateTime startTime, LocalDateTime endTime,
                                              ConfirmedReservation confirmedReservation) {
        return startTime.isAfter(confirmedReservation.getEndTime()) || endTime.isBefore(
                confirmedReservation.getStartTime());
    }

    private void updateConfirms() {
    }

    public List<ConfirmedReservationResponse> findAllConfirmedReservationsByDate(LocalDate date) {
        return confirmedRepository.findAllByReservationDate(date)
                .stream()
                .map(ConfirmedReservationResponse::of)
                .collect(Collectors.toList());
    }

    public void makeNewConfirms(LocalDate date) {
//        List<Reservation> confirmNeeded = reservationRepository.getReservationsConfirmNeeded(date, criteria);
//        reservationRepository.getReservationsConfirmNeeded(date, criteria)
//                .stream()
//                .filter(r -> findConfirmedReservation(r.getReservation().getReservationDate(), r.getReservation().getVideo(), r.getReservation().getStartTime(), r.getReservation().getEndTime()) == null)
//                .map(ConfirmedReservation::new)
//                .forEach(this::saveConfirm);

    }

    // 예외 발생 -> 시작시간과 종료시간이 start, end 내에 있고 예약 시간에 select 가 포함되어 있어야 한다!
    public List<TimetableResponse> getTimeTable(String start, String end, String select, String date) {
//        LocalDate localDate = LocalDate.parse(date);
//        Date startT=toDate(start);
//        Date endT=toDate(end);
//        List<Date> selects = Arrays.stream(select.split(","))
//                .map(this::toDate)
//                .collect(Collectors.toList());
//
//        //파라미터롤 넘어온 날짜에 예약된 모든 예약 내역 그룹 (시작시간, 종료시간, 영상 id) 으로 반환
//        List<ReservationCountDto> groupVideo = reservationRepository.getVideoGroupByDate(localDate);
//
//        return groupVideo
//                .stream()
//                .filter(g -> toDate(g.getReservation().getStartTime()).compareTo(startT) >= 0 && toDate(g.getReservation().getEndTime()).compareTo(endT) <= 0)
//                .filter(g -> filterSelection(selects, toDate(g.getReservation().getStartTime()), toDate(g.getReservation().getEndTime())))
//                .sorted(Comparator.comparing(g->toDate(g.getReservation().getStartTime())))
//                .map(g->TimetableResponse.of(g.getReservation(),g.getCount()))
//                .collect(Collectors.toList());

//        finals.stream()
//                .forEach(v-> System.out.println("v = " + v.getReservation().getId()+", "+v.getCount()));
        return null;

    }


    private Boolean filterSelection(List<Date> selects, Date start, Date end) {
        return selects.stream()
                .anyMatch(select -> start.compareTo(select) <= 0 && end.compareTo(select) >= 0);
    }

    public List<BestReservationResponse> getBestReservations() {
        LocalDate now = LocalDate.now();
//        return reservationRepository.getVideoGroupByDate(now)
//                .stream()
//                .filter(g->findConfirmedReservation(g.getReservation().getReservationDate(), g.getReservation().getVideo(), g.getReservation().getStartTime(),g.getReservation().getEndTime())==null)
//                .sorted(Comparator.comparing(ReservationCountDto::getCount).reversed()) //예약 많은 순 정렬
//                .limit(3)
//                .map(g -> BestReservationResponse.of(g.getReservation(), g.getCount()))
//                .collect(Collectors.toList());
        return null;
    }

    // 예외 발생
    public NextRunResponse findNextConfirm() {
        List<ConfirmedReservation> all = confirmedRepository.findAllByReservationDateGreaterThanEqual(LocalDate.now());
        Date today = new Date();

        if (all.isEmpty()) {
            return null; // 오늘 예약 영상이 없는 경우
        }

        //1. 현재 재생중인 영상 찾고, 있으면 그거 return ->
        //2. 다음 재생 예정 영상 찾고, 있으면 return
        //1,2 다 없으면 null return

        //시작시간 >= 지금시간 -> 대기중 / 시작시간 <= 지금시간 <=종료시간

//        ConfirmedReservation confirmedReservation = all.stream()
//                .filter(c->toDate(c.getReservationDate(),c.getStartTime()).compareTo(today)<=0&&today.compareTo(toDate(c.getReservationDate(),c.getEndTime()))<=0)
//                .findAny()
//                .orElse(null);
//
//        if(confirmedReservation!=null)
//            return NextRunResponse.of(confirmedReservation);
//
//        ConfirmedReservation nextReservation = all.stream()
//                .filter(c -> toDate(c.getReservationDate(), c.getStartTime()).compareTo(today) >= 0).min(Comparator.comparing(c -> toDate(c.getReservationDate(), c.getStartTime())))
//                .orElse(null);
//
//        if(nextReservation!=null)
//            return NextRunResponse.of(nextReservation);

        return null;

    }

    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public ConfirmedReservation findConfirmById(Long id) {
        return confirmedRepository.findById(id).orElse(null);
    }

    public int getReservationsCount(Reservation r) {
//        return reservationRepository.findAllByReservationDateAndStartTimeAndEndTimeAndVideo(r.getReservationDate(),r.getStartTime(),r.getEndTime(),r.getVideo()).size();
        return 0;
    }

    public int getReservationsCount(ConfirmedReservation r) {
//        return reservationRepository.findAllByReservationDateAndStartTimeAndEndTimeAndVideo(r.getReservationDate(),r.getStartTime(),r.getEndTime(),r.getVideo()).size();
        return 0;
    }

}
