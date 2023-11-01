package io.github.apprunner.tools;

import cn.hutool.core.date.StopWatch;
import io.github.apprunner.persistence.entity.AppDO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songyinyin
 * @since 2023/5/3 12:34
 */
public class AppRunnerContext {

    private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    public static final String STOP_WATCH = "stop_watch";
    public static final String APPS_META = "apps_meta";

    public static void setAppsMeta(List<AppDO> appsMeta) {
        put(APPS_META, appsMeta);
    }

    public static List<AppDO> getAppsMeta() {
        return (List<AppDO>) get(APPS_META, null);
    }

    public static void clearAppsMeta() {
        clear(APPS_META);
    }

    public static void setStopWatch(StopWatch stopWatch) {
        put(STOP_WATCH, stopWatch);
    }

    public static StopWatch getStopWatch() {
        return (StopWatch) get(STOP_WATCH, new StopWatch("default"));
    }

    /**
     * 设置上下文
     *
     * @param key   类型
     * @param value 值
     */
    public static void put(String key, Object value) {
        Map<String, Object> map = context.get();
        if (map == null) {
            map = new HashMap<>();
            context.set(map);
        }
        map.put(key, value);
    }

    /**
     * 获取某类型的上下文
     *
     * @param key          类型
     * @param defaultValue 默认值
     */
    public static Object get(String key, Object defaultValue) {
        Map<String, Object> map = context.get();
        if (map == null) {
            map = new HashMap<>();
            map.put(key, defaultValue);
            return defaultValue;
        }
        return map.get(key);
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
