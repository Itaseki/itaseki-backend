package com.example.backend.reservation;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.repository.ConfirmedReservationRepository;
import com.example.backend.reservation.repository.ReservationRepository;
import com.example.backend.video.domain.Video;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ConfirmedReservationRepository confirmedRepository;

    public void saveReservation(Reservation reservation){
        reservationRepository.save(reservation);
    }

    public void saveConfirm(ConfirmedReservation confirm){
        confirmedRepository.save(confirm);
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

//        Optional<ConfirmedReservation> match = confirms.stream()
//                .filter(confirm -> {
//                    try {
//                        return (finalStart.before(toDate(confirm.getReservationDate(), confirm.getEndTime()))&&(finalStart.after(toDate(confirm.getReservationDate(), confirm.getStartTime()))||finalStart.equals(toDate(confirm.getReservationDate(),confirm.getStartTime()))))
//                                || ((finalEnd.before((toDate(confirm.getReservationDate(), confirm.getEndTime())))||finalEnd.equals(toDate(confirm.getReservationDate(),confirm.getEndTime())))&&finalEnd.after(toDate(confirm.getReservationDate(), confirm.getStartTime())))
//                                || (finalStart.before(toDate(confirm.getReservationDate(), confirm.getStartTime()))&&finalEnd.after(toDate(confirm.getReservationDate(), confirm.getEndTime())));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    return false;
//                })
//                .findFirst();

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

    private Boolean checkDatetimeAvailability(String option,ConfirmedReservation confirm, Date reservationTime, String timeOption) throws ParseException {
        String confirmTime=timeOption.equals("start")?confirm.getStartTime():confirm.getEndTime();
        Date confirmedDate=toDate(confirm.getReservationDate(), confirmTime);
        return option.equals("before")?reservationTime.before(confirmedDate):reservationTime.after(confirmedDate);
    }

}
