package ru.khurry.voting.util;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

public class DateUtil {
//    @Autowired
    private static Clock clock = Clock.systemDefaultZone();

    public static void setClock(Clock clock) {
        DateUtil.clock = clock;
    }

    public static boolean isAfter(LocalTime thresholdTime) {
        return LocalTime.now(clock).isAfter(thresholdTime);
    }
}