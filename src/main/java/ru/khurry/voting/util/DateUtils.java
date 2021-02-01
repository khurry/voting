package ru.khurry.voting.util;

import java.time.Clock;
import java.time.LocalTime;

public class DateUtils {
//    @Autowired
    private static Clock clock = Clock.systemDefaultZone();

    public static void setClock(Clock clock) {
        DateUtils.clock = clock;
    }

    public static boolean isAfter(LocalTime thresholdTime) {
        return LocalTime.now(clock).isAfter(thresholdTime);
    }
}
