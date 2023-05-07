package com.github.dudiao.stm.tools;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.SystemPropsUtil;
import cn.hutool.http.HttpUtil;
import com.github.dudiao.stm.persistence.ToolDO;
import lombok.extern.slf4j.Slf4j;
import org.noear.snack.ONode;
import org.noear.solon.Solon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songyinyin
 * @since 2023/4/30 20:04
 */
@Slf4j
public class StmUtils {

    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    public static String getAppPath(ToolDO toolDO) {
        String appHome = Solon.cfg().get("stm.tools.app");
        if (appHome == null) {
            appHome = SystemPropsUtil.get("user.home") + "/.stm/app";
        }
        return "%s/%s/%s".formatted(appHome, toolDO.getName(), toolDO.getVersion());
    }

    public static boolean isDebugMode() {
        String stmDebug = Solon.cfg().get("stm.debug");
        return Solon.cfg().isDebugMode() || "1".equals(stmDebug);
    }

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }

    public static String getOsArch() {
        return System.getProperty("os.arch");
    }

    /**
     * 超时时间，单位毫秒
     */
    public static int timeout() {
        return  Solon.cfg().getInt("stm.api.timeout", 5) * 1000;
    }

    public static String apiUrl() {
        return Solon.cfg().get("stm.api.url");
    }

    public static List<ToolDO> apiList(String name) {
        String apiUrl = apiUrl();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String url = apiUrl + "/list";
        if (isDebugMode()) {
            log.info("{} request: {}", url, paramMap);
        }
        String response = HttpUtil.get(url, paramMap, timeout());
        if (isDebugMode()) {
            log.info("{} response: {}", url, response);
        }
        return ONode.loadStr(response).get("data").toObjectList(ToolDO.class);
    }
}
