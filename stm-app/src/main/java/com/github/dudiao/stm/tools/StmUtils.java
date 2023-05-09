package com.github.dudiao.stm.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import cn.hutool.http.HttpUtil;
import com.github.dudiao.stm.persistence.StmAppDO;
import com.github.dudiao.stm.persistence.StmAppVersionDO;
import com.github.dudiao.stm.plugin.StmException;
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

    public static final String API_LIST = "/list";
    public static final String API_LATEST_VERSION = "/latestVersion";

    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

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
        return Solon.cfg().getInt("stm.api.timeout", 5) * 1000;
    }

    public static String apiUrl() {
        return Solon.cfg().get("stm.api.url");
    }

    public static String getAppPath(StmAppDO stmAppDO) {
        String appHome = Solon.cfg().get("stm.tools.app");
        if (appHome == null) {
            appHome = SystemPropsUtil.get("user.home") + "/.stm/app";
        }
        return "%s/%s/%s".formatted(appHome, stmAppDO.getName(), stmAppDO.getVersion());
    }

    public static boolean isDebugMode() {
        String stmDebug = Solon.cfg().get("stm.debug");
        return Solon.cfg().isDebugMode() || "1".equals(stmDebug);
    }

    ///////////////////////// API /////////////////////////

    public static List<StmAppDO> apiList(String appName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", appName);
        ONode oNode = apiRequest(API_LIST, paramMap);
        return oNode.toObjectList(StmAppDO.class);
    }


    public static StmAppDO apiLatestVersion(String appName, String version) {
        if (StrUtil.isBlank(appName)) {
            throw new StmException("appName 不能为空");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appName", appName);
        paramMap.put("version", version);
        ONode oNode = apiRequest(API_LATEST_VERSION, paramMap);
        StmAppDO stmAppDO = oNode.toObject(StmAppDO.class);
        if (stmAppDO == null) {
            throw new StmException("未找到应用：%s".formatted(appName));
        }
        stmAppDO.setVersion(stmAppDO.getAppLatestVersion().getVersion());
        return stmAppDO;
    }

    private static ONode apiRequest(String path, Map<String, Object> paramMap) {
        String apiUrl = apiUrl();
        String url = apiUrl + path;
        if (isDebugMode()) {
            log.info("{} request: {}", url, paramMap);
        }
        String response = HttpUtil.get(url, paramMap, timeout());
        if (isDebugMode()) {
            log.info("{} response: {}", url, response);
        }
        ONode oNode = ONode.loadStr(response);
        int status = oNode.get("status").getInt();
        if (status != 0) {
            throw new StmException("请求失败(%s)：%s".formatted(status, oNode.get("msg").getString()));
        }
        return oNode.get("data");
    }
}
