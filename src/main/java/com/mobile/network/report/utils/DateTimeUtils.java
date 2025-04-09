package com.mobile.network.report.utils;

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
}
