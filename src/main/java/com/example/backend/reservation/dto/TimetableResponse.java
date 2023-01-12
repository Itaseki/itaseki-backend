package com.example.backend.reservation.dto;

import com.example.backend.reservation.converter.TimeConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TimetableResponse {
    private Long id;
    private String title;
    private String reservationDate;
    private String startTime;
    private String endTime;
    private String runTime;
    private Long count;

    public static TimetableResponse fromDto(ReservationGroupDto dto) {
        return TimetableResponse.builder()
                .id(dto.getVideo().getId())
                .title(dto.getVideo().getDescription())
                .reservationDate(dto.getReservationDate().toString())
                .startTime(TimeConverter.convertToString(dto.getStartTime()))
                .endTime(TimeConverter.convertToString(dto.getEndTime()))
                .runTime(dto.getVideo().getConvertedRuntime())
                .count(dto.getReservationCount())
                .build();
    }


}
