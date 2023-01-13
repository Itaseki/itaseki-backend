package com.example.backend.reservation.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeConverter {
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-ddHH%smm";
    private static final String SPACE_DELIMITER = " ";
    private static final String COLON_DELIMITER = ":";

    private TimeConverter() {}

    public static String convertToString(LocalDateTime time) {
        return time.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
    }

    public static LocalDateTime convertToLocalTime(String date, String time) {
        String dateTime = date + time;
        if (time.contains(COLON_DELIMITER)) {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(String.format(DATE_TIME_FORMAT, COLON_DELIMITER)));
        }
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(String.format(DATE_TIME_FORMAT, SPACE_DELIMITER)));
    }
}
