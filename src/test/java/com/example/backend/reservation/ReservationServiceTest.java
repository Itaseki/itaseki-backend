package com.example.backend.reservation;

import static org.assertj.core.api.Assertions.*;

import com.example.backend.reservation.dto.ReservationDto;
import com.example.backend.reservation.exception.WrongEndTimeException;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReservationServiceTest {
    @Autowired
    private ReservationService reservationService;

    @DisplayName("예약 종료 시간이 시작 시간의 다음 날로 넘어가면 예외가 발생한다")
    @ParameterizedTest
    @CsvSource(value = {"2023-01-01,23:50,01:00", "2023-01-31,23:50,01:00"})
    void endTimeIsNextDayOfStartTime(String date, String start, String end) {
        ReservationDto dto = createReservationRequest(date, start, end);

        assertThatThrownBy(() -> reservationService.saveReservation(dto, null, null))
                .isInstanceOf(WrongEndTimeException.class);
    }

    private ReservationDto createReservationRequest(String date, String startTime, String endTime) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(1L);
        reservationDto.setReservationDate(date);
        reservationDto.setStartTime(startTime);
        reservationDto.setEndTime(endTime);
        return reservationDto;
    }
}