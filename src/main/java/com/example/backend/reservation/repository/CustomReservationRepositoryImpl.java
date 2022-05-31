package com.example.backend.reservation.repository;

import com.example.backend.reservation.domain.Reservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.example.backend.reservation.domain.QReservation.reservation;


@RequiredArgsConstructor
public class CustomReservationRepositoryImpl implements CustomReservationRepository{
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Reservation> getReservationsConfirmNeeded(LocalDate criteriaDate, Long confirmCount) {
        return jpaQueryFactory
                .selectFrom(reservation)
                .where(reservation.reservationDate.goe(criteriaDate)) //날짜가 오늘 날짜 이후 (이전 날짜는 업데이트 필요 없음)
                .groupBy(reservation.reservationDate, reservation.video, reservation.startTime, reservation.endTime)
                .having(reservation.count().goe(confirmCount))
                .fetch();
    }

}
