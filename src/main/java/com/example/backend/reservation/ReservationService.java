package com.example.backend.reservation;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.dto.BestReservationResponse;
import com.example.backend.reservation.dto.ReservationCountDto;
import com.example.backend.reservation.dto.TimetableResponse;
import com.example.backend.reservation.repository.ConfirmedReservationRepository;
import com.example.backend.reservation.repository.ReservationRepository;
import com.example.backend.video.domain.Video;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ConfirmedReservationRepository confirmedRepository;

    public void saveReservation(Reservation reservation){
        reservationRepository.save(reservation);
        LocalDate localDate = reservation.getReservationDate();
        Long criteria=2L;
        makeNewConfirms(localDate,criteria);
    }

    public void saveConfirm(ConfirmedReservation confirm){
        ConfirmedReservation save = confirmedRepository.save(confirm);
        System.out.println("save = " + save.getId());
    }

    public Boolean checkReservationConflict(Reservation reservation){
        LocalDate date=reservation.getReservationDate();
        String sTime=reservation.getStartTime();
        String eTime=reservation.getEndTime();


        Date start;
        Date end;
        try {
            start = toDate(date,sTime);
            end=toDate(date,eTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        List<ConfirmedReservation> confirms = confirmedRepository.findAllByReservationDate(date);
        //시작시간이 둘 사이 / 종료시간이 둘 사이

        Date finalStart = start;
        Date finalEnd = end;

        //예약 확정 내역들 -> 각각 startTime, endTime 으로 mapping
        //그 값들에 대해 filter 적용
        //forEach -> filter 되나?

        //compareTo: 같으면 0, 이후 날짜면 양수, 이전 날짜면 음수

        Optional<ConfirmedReservation> match = confirms.stream()
                .filter(confirm -> {
                    try {
                        return (finalStart.before(toDate(confirm.getReservationDate(), confirm.getEndTime())) && (finalStart.compareTo(toDate(confirm.getReservationDate(), confirm.getStartTime())) >= 0))
                                || (finalEnd.compareTo(toDate(confirm.getReservationDate(), confirm.getEndTime())) <= 0 && finalEnd.after(toDate(confirm.getReservationDate(), confirm.getStartTime())))
                                || (finalStart.before(toDate(confirm.getReservationDate(), confirm.getStartTime())) && finalEnd.after(toDate(confirm.getReservationDate(), confirm.getEndTime())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .findFirst();
        return match.isPresent(); //충돌되는거 있으면 true, 아니면 false 반환

    }

    //날짜, 사용자, 영상으로 예약 찾기 -> 당일 중복 예약 막기
    public Reservation findReservationByDateAndVideoAndUser(LocalDate date, Video video, User user){
        List<Reservation> reservations = reservationRepository.findByReservationDateAndVideo(date, video);
        Optional<Reservation> foundReservation = reservations.stream()
                .filter(reservation -> reservation.getUser().equals(user))
                .findFirst();
        return foundReservation.orElse(null);
    }

    public ConfirmedReservation findConfirmedReservation(LocalDate date, Video video, String start, String end){
        return confirmedRepository.findByReservationDateAndStartTimeAndEndTimeAndVideo(date, start, end, video);
    }

    private Date toDate(LocalDate date, String time) throws ParseException{
        SimpleDateFormat form=new SimpleDateFormat("yy-MM-ddHH:mm");
        String reservationDate=date.toString();
        return form.parse(reservationDate+time);
    }

    private Date toDate(String time){
        SimpleDateFormat form=new SimpleDateFormat("HH mm");
        if(time.contains(":"))
            form=new SimpleDateFormat("HH:mm");
        try {
            return form.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void makeNewConfirms(LocalDate date, Long criteria){
//        List<Reservation> confirmNeeded = reservationRepository.getReservationsConfirmNeeded(date, criteria);
        reservationRepository.getReservationsConfirmNeeded(date, criteria)
                .stream()
                .filter(reservation -> findConfirmedReservation(reservation.getReservationDate(), reservation.getVideo(), reservation.getStartTime(), reservation.getEndTime()) == null)
                .map(ConfirmedReservation::new)
                .forEach(this::saveConfirm);

    }

    public List<TimetableResponse> test(String start, String end, String select, String date){
        LocalDate localDate = LocalDate.parse(date);
        Date startT=toDate(start);
        Date endT=toDate(end);
        List<Date> selects = Arrays.stream(select.split(","))
                .map(this::toDate)
                .collect(Collectors.toList());

        List<ReservationCountDto> groupVideo = reservationRepository.getDateReservationGroupVideo(localDate);

        return groupVideo
                .stream()
                .filter(g -> toDate(g.getReservation().getStartTime()).compareTo(startT) >= 0 && toDate(g.getReservation().getEndTime()).compareTo(endT) <= 0)
                .filter(g -> filterSelection(selects, toDate(g.getReservation().getStartTime()), toDate(g.getReservation().getEndTime())))
                .sorted(Comparator.comparing(g->toDate(g.getReservation().getStartTime())))
                .map(g->TimetableResponse.of(g.getReservation(),g.getCount()))
                .collect(Collectors.toList());

//        finals.stream()
//                .forEach(v-> System.out.println("v = " + v.getReservation().getId()+", "+v.getCount()));

    }


    private Boolean filterSelection(List<Date> selects, Date start, Date end){
        return selects.stream()
                .anyMatch(select->start.compareTo(select)<=0&&end.compareTo(select)>=0);
    }

    public List<BestReservationResponse> getBestReservations(){
        LocalDate now = LocalDate.now();
//        String s="2022-05-26";
//        now = LocalDate.parse(s);
        return reservationRepository.getDateReservationGroupVideo(now)
                .stream()
                .sorted(Comparator.comparing(ReservationCountDto::getCount).reversed())
                .limit(3)
                .map(g -> BestReservationResponse.of(g.getReservation(), g.getCount()))
                .collect(Collectors.toList());
    }

}
