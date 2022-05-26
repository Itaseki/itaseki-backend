package com.example.backend.reservation;

import com.example.backend.reservation.domain.ConfirmedReservation;
import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.repository.ConfirmedReservationRepository;
import com.example.backend.reservation.repository.ReservationRepository;
import com.example.backend.video.domain.Video;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ConfirmedReservationRepository confirmedRepository;

    public Reservation saveReservation(Reservation reservation){
        Boolean available = checkReservationAvailability(reservation);
        System.out.println("available = " + available);
        Reservation made=null;
        if(available)
            made=reservationRepository.save(reservation);
        return made;
    }

    public void saveConfirm(ConfirmedReservation confirm){
        confirmedRepository.save(confirm);
    }

    public Boolean checkReservationAvailability(Reservation reservation){
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

        //forEach -> filter 되나?
        Optional<ConfirmedReservation> match = confirms.stream()
                .filter(confirm -> {
                    try {
                        return finalStart.before(toDate(confirm.getReservationDate(), confirm.getEndTime()))&&finalStart.after(toDate(confirm.getReservationDate(), confirm.getStartTime()))
                                || finalEnd.before(toDate(confirm.getReservationDate(), confirm.getEndTime()))&&finalEnd.after(toDate(confirm.getReservationDate(), confirm.getStartTime()))
                                || finalStart.before(toDate(confirm.getReservationDate(), confirm.getStartTime()))&&finalEnd.after(toDate(confirm.getReservationDate(), confirm.getEndTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .findFirst();
        return match.isEmpty();

    }

    private Date toDate(LocalDate date, String time) throws ParseException{
        SimpleDateFormat form=new SimpleDateFormat("yy-MM-ddHH:mm");
        String reservationDate=date.toString();
        return form.parse(reservationDate+time);
    }

    public void calcEndTime(int hour, int min, Video video){
        Integer runtimeHour = video.getRuntimeHour();
        Integer runtimeMin = video.getRuntimeMin();
        Integer runtimeSec = video.getRuntimeSec();
        hour+=runtimeHour;
        if(runtimeSec>0){
            runtimeMin++;
        }
        if(runtimeMin>0){
            min+=(runtimeMin+9)/10*10;
        }
        if(min>60){
            hour++;
            min-=60;
        }

    }
}
