package org.masouras.util;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtils {
    private static final ZoneId TARGET_ZONE = ZoneId.of("Europe/Athens");
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSS";

    public static @Nullable String formatToAthens(@Nullable ZonedDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.withZoneSameInstant(TARGET_ZONE).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }
    public static @Nullable String formatToAthens(@Nullable Instant instant) {
        if (instant == null) return null;
        return instant.atZone(ZoneId.systemDefault()).withZoneSameInstant(TARGET_ZONE).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }
    public static @Nullable String formatToAthens(@Nullable LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(TARGET_ZONE).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

}
