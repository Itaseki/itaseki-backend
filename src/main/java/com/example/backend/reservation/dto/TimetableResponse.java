package com.example.backend.reservation.dto;

import com.example.backend.reservation.domain.Reservation;
import com.example.backend.video.domain.Video;
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

    public static TimetableResponse of(Reservation reservation, Long count){
        Video video = reservation.getVideo();
        return TimetableResponse.builder()
                .id(video.getId())
                .title(video.getDescription())
                .reservationDate(reservation.getReservationDate().toString())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .runTime(getVideoRuntimeString(video))
                .count(count)
                .build();
    }

    private static String getVideoRuntimeString(Video video){
        String h = video.getRuntimeHour()<10?"0"+video.getRuntimeHour():video.getRuntimeHour().toString();
        String m = video.getRuntimeMin()<10?"0"+video.getRuntimeMin():video.getRuntimeMin().toString();
        String s = video.getRuntimeSec()<10?"0"+video.getRuntimeSec():video.getRuntimeSec().toString();
        return h+":"+m+":"+s;
    }

}
