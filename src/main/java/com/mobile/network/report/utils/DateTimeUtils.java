package com.mobile.network.report.utils;

import java.time.Duration;
import java.time.Instant;
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
}
