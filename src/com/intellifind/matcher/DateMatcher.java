package com.intellifind.matcher;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateMatcher {
    public static double computeDateScore(LocalDate lostDate, LocalDate foundDate) {
        if (lostDate == null || foundDate == null) return 0.0;

        long daysDiff = ChronoUnit.DAYS.between(lostDate, foundDate);
        if (daysDiff < 0) {
            return (daysDiff >= -1) ? 0.70 : 0.10;
        }
        if (daysDiff == 0) return 1.0;
        if (daysDiff <= 2) return 0.90;
        if (daysDiff <= 5) return 0.75;
        if (daysDiff <= 10) return 0.50;
        if (daysDiff <= 30) return 0.25;
        return 0.05;
    }
}