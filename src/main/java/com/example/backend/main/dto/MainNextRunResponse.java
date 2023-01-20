package com.example.backend.main.dto;

import com.example.backend.reservation.dto.NextRunResponse;
import lombok.Getter;

@Getter
public class MainNextRunResponse {
    private final long reservationId;
    private final String startTime;

    private MainNextRunResponse(long id, String startTime) {
        this.reservationId = id;
        this.startTime = startTime;
    }

    public static MainNextRunResponse ofReservation(NextRunResponse reservation) {
        if (reservation == null) {
            return null;
        }
        return new MainNextRunResponse(reservation.getReservationId(), reservation.getStartTime());
    }
}
