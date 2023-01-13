package com.example.backend.reservation;

import com.example.backend.reservation.converter.TimeConverter;
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
    private final int BEST_LIMIT = 3;

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

        makeNewConfirms(LocalDate.now());
    }

    private void saveConfirm(ConfirmedReservation confirm) {
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

    public List<ConfirmedReservationResponse> findAllConfirmedReservationsByDate(LocalDate date) {
        return confirmedRepository.findAllByReservationDate(date)
                .stream()
                .sorted(Comparator.comparing(ConfirmedReservation::getStartTime))
                .map(ConfirmedReservationResponse::of)
                .collect(Collectors.toList());
    }

    private void makeNewConfirms(LocalDate date) {
        reservationRepository.findReservationsConfirmNeeded(date, CONFIRM_CRITERIA)
                .stream()
                .filter(reservation -> !isAlreadySameConfirmedReservationExist(reservation.getVideo(), reservation.getStartTime(), reservation.getEndTime()))
                .map(ConfirmedReservation::fromDto)
                .forEach(this::saveConfirm);
    }

    public List<TimetableResponse> getTimeTable(String start, String end, String select, String date) {
        LocalDateTime startTime = TimeConverter.convertToLocalTime(date, start);
        LocalDateTime endTime = TimeConverter.convertToLocalTime(date, end);
        List<LocalDateTime> selectedTimes = convertSelectedTimes(date, select);

        return reservationRepository.findAllByTimeCondition(startTime, endTime, selectedTimes)
                .stream()
                .map(TimetableResponse::fromDto)
                .collect(Collectors.toList());
    }

    private List<LocalDateTime> convertSelectedTimes(String date, String select) {
        return Arrays.stream(select.split(","))
                .map(time -> TimeConverter.convertToLocalTime(date, time))
                .collect(Collectors.toList());
    }

    public List<BestReservationResponse> getBestReservations() {
        return reservationRepository.findReservationByDate(LocalDate.now())
                .stream()
                .sorted(Comparator.comparing(ReservationGroupDto::getReservationCount).reversed())
                .limit(BEST_LIMIT)
                .map(BestReservationResponse::fromDto)
                .collect(Collectors.toList());
    }

    public NextRunResponse findNextConfirm() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);

        // 재생시간이 50분 까지면, 50분 59초 까지를 의미하는지 아니면 49분 59초 까지를 의미하는지
        ConfirmedReservation confirmedReservation =
                confirmedRepository.findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(now, now)
                .orElse(null);

        if (confirmedReservation != null) {
            return NextRunResponse.of(confirmedReservation);
        }

        List<ConfirmedReservation> confirms = confirmedRepository.findByStartTimeGreaterThanEqualOrderByStartTime(now);

        return confirms.stream()
                .findFirst()
                .map(NextRunResponse::of)
                .orElse(null);
    }

    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public ConfirmedReservation findConfirmById(Long id) {
        return confirmedRepository.findById(id).orElse(null);
    }

    public int getReservationsCount(Reservation r) {
        return reservationRepository.findAllByReservationDateAndStartTimeAndEndTimeAndVideo(r.getReservationDate(),r.getStartTime(),r.getEndTime(),r.getVideo()).size();
    }

    public int getReservationsCount(ConfirmedReservation r) {
        return reservationRepository.findAllByReservationDateAndStartTimeAndEndTimeAndVideo(r.getReservationDate(),r.getStartTime(),r.getEndTime(),r.getVideo()).size();
    }

}
