package com.oner;

import java.time.Duration;
import java.time.Instant;

public class TimeIt {

    static void timeit(ThrowingRunnable opr) {
        Instant start = Instant.now();
        try {
            opr.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Instant end = Instant.now();
            System.out.println("Duration (millis) : " + Duration.between(start, end).toMillis());
        }
    }
}
