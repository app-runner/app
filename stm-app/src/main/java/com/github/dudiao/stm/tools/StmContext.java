package com.github.dudiao.stm.tools;

import cn.hutool.core.date.StopWatch;

import java.util.HashMap;
import java.util.Map;

/**
 * @author songyinyin
 * @since 2023/5/3 12:34
 */
public class StmContext {

    private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    public static final String STOP_WATCH = "stop_watch";

    public static void setStopWatch(StopWatch stopWatch) {
        Map<String, Object> map = context.get();
        if (map == null) {
            map = new HashMap<>();
            context.set(map);
        }
        map.put(STOP_WATCH, stopWatch);
    }

    public static StopWatch getStopWatch() {
        Map<String, Object> map = context.get();
        if (map == null) {
            StopWatch stopWatch = new StopWatch("default");
            map = new HashMap<>();
            map.put(STOP_WATCH, stopWatch);
            return stopWatch;
        }
        return (StopWatch) map.get(STOP_WATCH);
    }

    public static void clear(String key) {
        Map<String, Object> map = context.get();
        if (map == null) {
            return;
        }
        map.remove(key);
    }

    public static void clear() {
        context.remove();
    }


}
