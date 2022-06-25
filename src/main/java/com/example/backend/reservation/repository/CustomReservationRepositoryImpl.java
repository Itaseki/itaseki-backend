package com.example.backend.reservation.repository;

import com.example.backend.reservation.domain.Reservation;
import com.example.backend.reservation.dto.ReservationCountDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;

import static com.example.backend.reservation.domain.QReservation.reservation;


@RequiredArgsConstructor
public class CustomReservationRepositoryImpl implements CustomReservationRepository{
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<ReservationCountDto> getReservationsConfirmNeeded(LocalDate criteriaDate, Long confirmCount) {
        return jpaQueryFactory
                .select(Projections.fields(ReservationCountDto.class, reservation.as("reservation"), reservation.count().as("count")))
                .from(reservation)
                .where(reservation.reservationDate.goe(criteriaDate)) //날짜가 오늘 날짜 이후 (이전 날짜는 업데이트 필요 없음)
                .groupBy(reservation.reservationDate, reservation.video, reservation.startTime, reservation.endTime)
                .having(reservation.count().goe(confirmCount))
                .fetch();
    }

    @Override
    public List<ReservationCountDto> getVideoGroupByDate(LocalDate date) {
        //여기서 날짜, 시작시간, 종료시간, 영상으로 같은 비디오 다 groupBy 해주고 그 결과에서 startTIme, endTime 비교해서 선택시간만 반환
        return jpaQueryFactory
                .select(Projections.fields(ReservationCountDto.class, reservation.as("reservation"), reservation.count().as("count")))
                .from(reservation)
                .where(reservation.reservationDate.eq(date))
                .groupBy(reservation.reservationDate, reservation.video, reservation.startTime, reservation.endTime)
                .fetch();
    }

}
