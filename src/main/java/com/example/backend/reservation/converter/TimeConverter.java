package com.example.backend.reservation.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeConverter {
    private static final String TIME_FORMAT = "HH:mm";

    private TimeConverter() {}

    public static String convertToString(LocalDateTime time) {
        return time.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
    }
}
