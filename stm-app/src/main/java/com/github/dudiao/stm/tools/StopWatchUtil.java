package com.github.dudiao.stm.tools;

import cn.hutool.core.date.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * @author songyinyin
 * @since 2023/5/2 18:52
 */
public class StopWatchUtil {

    private static final long NANO_SCALE   = 1L;
    private static final long MICRO_SCALE  = 1000L * NANO_SCALE;
    private static final long MILLI_SCALE  = 1000L * MICRO_SCALE;
    private static final long SECOND_SCALE = 1000L * MILLI_SCALE;
    private static final long MINUTE_SCALE = 60L * SECOND_SCALE;
    private static final long HOUR_SCALE   = 60L * MINUTE_SCALE;
    private static final long DAY_SCALE    = 24L * HOUR_SCALE;

    public static String prettyPrint(StopWatch stopWatch) {
        long totalTimeNanos = stopWatch.getTotalTimeNanos();

        if (totalTimeNanos >= SECOND_SCALE) {
            return stopWatch.prettyPrint(TimeUnit.SECONDS);
        }
        if (totalTimeNanos >= MILLI_SCALE) {
            return stopWatch.prettyPrint(TimeUnit.MILLISECONDS);
        }
        if (totalTimeNanos >= MICRO_SCALE) {
            return stopWatch.prettyPrint(TimeUnit.MICROSECONDS);
        }
        return stopWatch.prettyPrint();
    }

}
