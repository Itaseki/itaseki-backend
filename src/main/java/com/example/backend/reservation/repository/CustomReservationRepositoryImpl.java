package com.example.backend.reservation.repository;

import com.example.backend.reservation.dto.ReservationCountDto;
import com.example.backend.reservation.dto.ReservationGroupDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;

import static com.example.backend.reservation.domain.QReservation.reservation;


@RequiredArgsConstructor
public class CustomReservationRepositoryImpl implements CustomReservationRepository {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<ReservationCountDto> findReservationsConfirmNeeded(LocalDate criteriaDate, Long confirmCount) {
        return jpaQueryFactory
                .select(Projections.fields(ReservationCountDto.class, reservation.as("reservation"),
                        reservation.count().as("count")))
                .from(reservation)
                .where(reservation.reservationDate.goe(criteriaDate)) //날짜가 오늘 날짜 이후 (이전 날짜는 업데이트 필요 없음)
                .groupBy(reservation.reservationDate, reservation.video, reservation.startTime, reservation.endTime)
                .having(reservation.count().goe(confirmCount))
                .fetch();
    }

    @Override
    public List<ReservationGroupDto> findReservationByDate(LocalDate date) {
        return jpaQueryFactory
                .select(Projections.fields(ReservationGroupDto.class,
                        reservation.video.as("video"),
                        reservation.startTime.as("startTime"),
                        reservation.endTime.as("endTime"),
                        reservation.reservationDate.as("reservationDate"),
                        reservation.count().as("reservationCount")))
                .from(reservation)
                .where(reservation.reservationDate.eq(date))
                .groupBy(reservation.reservationDate, reservation.video, reservation.startTime, reservation.endTime)
                .fetch();
    }

    @Override
    public List<ReservationGroupDto> findAllByTimeCondition(LocalDateTime startTime, LocalDateTime endTime,
                                                            List<LocalDateTime> selection) {
        return jpaQueryFactory
                .select(Projections.fields(ReservationGroupDto.class,
                        reservation.video.as("video"),
                        reservation.startTime.as("startTime"),
                        reservation.endTime.as("endTime"),
                        reservation.reservationDate.as("reservationDate"),
                        reservation.count().as("reservationCount")))
                .from(reservation)
                .where(reservationIsBetweenStartAndEnd(startTime, endTime)
                        .and(reservationIsIncludingSelection(selection)))
                .groupBy(reservation.startTime, reservation.endTime, reservation.video, reservation.reservationDate)
                .orderBy(reservation.startTime.asc())
                .fetch();
    }

    private BooleanExpression reservationIsBetweenStartAndEnd(LocalDateTime startTime, LocalDateTime endTime) {
        return reservation.startTime.goe(startTime).and(reservation.endTime.loe(endTime));
    }

    private BooleanExpression reservationIsIncludingSelection(List<LocalDateTime> selection) {
        return Expressions.anyOf(selection.stream()
                .map(this::reservationIncludingSelectedTime)
                .toArray(BooleanExpression[]::new));
        // 각 selectedTime 조건을 만족하는 Reservation 의 합집합 --> 모든 selectedTime 조건들 중, 단 하나의 시간이라도 예약 시간 내에 포함된다면 true
    }

    private BooleanExpression reservationIncludingSelectedTime(LocalDateTime selectedTime) {
        return reservation.startTime.loe(selectedTime).and(reservation.endTime.goe(selectedTime));
    }

}
