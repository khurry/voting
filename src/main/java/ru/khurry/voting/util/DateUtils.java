package ru.khurry.voting.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

public class DateUtils {
    private static Clock clock = Clock.systemDefaultZone();

    public static void setClock(Clock clock) {
        DateUtils.clock = clock;
    }

    public static boolean isAfter(LocalTime thresholdTime, LocalDate voteDate) {
        return LocalTime.now(clock).isAfter(thresholdTime) || LocalDate.now().isAfter(voteDate);
    }
}
