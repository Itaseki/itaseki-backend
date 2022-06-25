package com.example.backend.reservation;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.dto.*;
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

        start = toDate(date,sTime);
        end=toDate(date,eTime);

        List<ConfirmedReservation> confirms = confirmedRepository.findAllByReservationDate(date);
        //시작시간이 둘 사이 / 종료시간이 둘 사이

        Optional<ConfirmedReservation> match = confirms.stream()
                .filter(confirm -> (start.before(toDate(confirm.getReservationDate(), confirm.getEndTime())) && (start.compareTo(toDate(confirm.getReservationDate(), confirm.getStartTime())) >= 0))
                               || (end.compareTo(toDate(confirm.getReservationDate(), confirm.getEndTime())) <= 0 && end.after(toDate(confirm.getReservationDate(), confirm.getStartTime())))
                               || (start.before(toDate(confirm.getReservationDate(), confirm.getStartTime())) && end.after(toDate(confirm.getReservationDate(), confirm.getEndTime()))))
                .findFirst();
        return match.isPresent(); //충돌되는거 있으면 true, 아니면 false 반환

    }

    public Boolean checkEndTimeValidate(Reservation reservation){
        return toDate(reservation.getStartTime()).compareTo(toDate(reservation.getEndTime())) < 0;
    }

    //날짜, 사용자, 영상으로 예약 찾기 -> 당일 중복 예약 막기
    //한 사용자 - 하루에 하나의 영상만 예약! => findReservationByDateAndUser 로 변경
    public Reservation findReservationByDateAndUser(LocalDate date, User user){
        return reservationRepository.findByReservationDateAndUser(date, user).orElse(null);
    }

    public ConfirmedReservation findConfirmedReservation(LocalDate date, Video video, String start, String end){
        return confirmedRepository.findByReservationDateAndStartTimeAndEndTimeAndVideo(date, start, end, video);
    }

    public List<ConfirmedReservationResponse> findAllConfirmedReservationsByDate(LocalDate date){
        return confirmedRepository.findAllByReservationDate(date)
                .stream()
                .map(ConfirmedReservationResponse::of)
                .collect(Collectors.toList());
    }

    private Date toDate(LocalDate date, String time){
        SimpleDateFormat form=new SimpleDateFormat("yy-MM-ddHH:mm");
        String reservationDate=date.toString();
        try{
            return form.parse(reservationDate+time);
        }catch (ParseException e){
            //잘못된 문자열 type 이라고 exception handling
            e.printStackTrace();
            return null;
        }
    }

    private Date toDate(String time){
        SimpleDateFormat form=new SimpleDateFormat("HH mm");
        if(time.contains(":"))
            form=new SimpleDateFormat("HH:mm");
        try {
            return form.parse(time);
        } catch (ParseException e) {
            //잘못된 문자열 type 이라고 exception handling
            e.printStackTrace();
            return null;
        }
    }

    public void makeNewConfirms(LocalDate date, Long criteria){
//        List<Reservation> confirmNeeded = reservationRepository.getReservationsConfirmNeeded(date, criteria);
        reservationRepository.getReservationsConfirmNeeded(date, criteria)
                .stream()
                .filter(r -> findConfirmedReservation(r.getReservation().getReservationDate(), r.getReservation().getVideo(), r.getReservation().getStartTime(), r.getReservation().getEndTime()) == null)
                .map(ConfirmedReservation::new)
                .forEach(this::saveConfirm);

    }

    public List<TimetableResponse> getTimeTable(String start, String end, String select, String date){
        LocalDate localDate = LocalDate.parse(date);
        Date startT=toDate(start);
        Date endT=toDate(end);
        List<Date> selects = Arrays.stream(select.split(","))
                .map(this::toDate)
                .collect(Collectors.toList());

        //파라미터롤 넘어온 날짜에 예약된 모든 예약 내역 그룹 (시작시간, 종료시간, 영상 id) 으로 반환
        List<ReservationCountDto> groupVideo = reservationRepository.getVideoGroupByDate(localDate);

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
        return reservationRepository.getVideoGroupByDate(now)
                .stream()
                .filter(g->findConfirmedReservation(g.getReservation().getReservationDate(), g.getReservation().getVideo(), g.getReservation().getStartTime(),g.getReservation().getEndTime())==null)
                .sorted(Comparator.comparing(ReservationCountDto::getCount).reversed()) //예약 많은 순 정렬
                .limit(3)
                .map(g -> BestReservationResponse.of(g.getReservation(), g.getCount()))
                .collect(Collectors.toList());
    }

    public NextRunResponse findNextConfirm() {
        List<ConfirmedReservation> all = confirmedRepository.findAllByReservationDateGreaterThanEqual(LocalDate.now());
        Date today = new Date();

        if (all.isEmpty())
            return null;

        //1. 현재 재생중인 영상 찾고, 있으면 그거 return
        //2. 다음 재생 예정 영상 찾고, 있으면 return
        //1,2 다 없으면 null return

        //시작시간 >= 지금시간 -> 대기중 / 시작시간 <= 지금시간 <=종료시간

        ConfirmedReservation confirmedReservation = all.stream()
                .filter(c->toDate(c.getReservationDate(),c.getStartTime()).compareTo(today)<=0&&today.compareTo(toDate(c.getReservationDate(),c.getEndTime()))<=0)
                .findAny()
                .orElse(null);

        if(confirmedReservation!=null)
            return NextRunResponse.of(confirmedReservation);

        ConfirmedReservation nextReservation = all.stream()
                .filter(c -> toDate(c.getReservationDate(), c.getStartTime()).compareTo(today) >= 0).min(Comparator.comparing(c -> toDate(c.getReservationDate(), c.getStartTime())))
                .orElse(null);

        if(nextReservation!=null)
            return NextRunResponse.of(nextReservation);

        return null;

    }

}
