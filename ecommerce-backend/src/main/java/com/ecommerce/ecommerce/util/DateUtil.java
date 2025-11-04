package com.ecommerce.ecommerce.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
    public static final DateTimeFormatter DATE_ONLY_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_FORMAT) : "";
    }

    public static String formatDateOnly(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_ONLY_FORMAT) : "";
    }

    public static String formatISO(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_FORMAT) : "";
    }

    public static boolean isWithinLastDays(LocalDateTime dateTime, int days) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now().minus(days, ChronoUnit.DAYS));
    }

    public static boolean isOlderThanDays(LocalDateTime dateTime, int days) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now().minus(days, ChronoUnit.DAYS));
    }

    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().atStartOfDay() : null;
    }

    public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().atTime(23, 59, 59) : null;
    }
}
