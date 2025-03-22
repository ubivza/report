package com.mobile.network.report.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import lombok.experimental.UtilityClass;

/**
 * Вспомогательный класс для работы со временем
 */
@UtilityClass
public class DateTimeUtils {

    public Instant currentInstant() {
        return Instant.now();
    }

    public Duration calculateCallDuration(Instant callStartTime, Instant callEndTime) {
        if (callStartTime != null && callEndTime != null) {
            return Duration.between(callStartTime, callEndTime);
        }
        return null;
    }

    public Instant getStartOfMonth(YearMonth yearMonth) {
        return yearMonth.atDay(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
    }

    public Instant getEndOfMonth(YearMonth yearMonth) {
        return yearMonth.atEndOfMonth().atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC);
    }
}
