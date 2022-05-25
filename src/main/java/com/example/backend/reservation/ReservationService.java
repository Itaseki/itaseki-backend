package com.example.backend.reservation;

import com.example.backend.reservation.dto.ReservationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public void saveReservation(Reservation reservation){
        reservationRepository.save(reservation);
    }

//    private void confirmReservation(Reservation reservation){
//        Integer startHour = reservation.getStartHour();
//        Integer startMin = reservation.getStartMin();
//        Integer endHour = reservation.getEndHour();
//        Integer endMin = reservation.getEndMin();
//        //시작시간, 종료시간 기준으로 모든 예약 내역 돌면서 동일 시간에 해당하는 예약 내역 찾기
//    }
//
//    public Boolean checkReservationAvailability(Reservation reservation){
//        //확정된 예약 내역을 따로 저장해주어야 하는데,,,,, (하루동안만 유지)
//        // 1) 캐시서버 2) 데이터베이스 테이블
//        Integer startHour = reservation.getStartHour();
//        Integer startMin = reservation.getStartMin();
//        Integer endHour = reservation.getEndHour();
//        Integer endMin = reservation.getEndMin();
//    }
}
