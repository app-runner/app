package io.github.apprunner.tools;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import io.github.apprunner.persistence.ApplicationType;
import io.github.apprunner.persistence.StmAppDO;
import io.github.apprunner.plugin.StmException;
import lombok.extern.slf4j.Slf4j;
import org.noear.snack.ONode;
import org.noear.solon.Solon;

import java.io.File;
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
    public static final String API_GET_APP_RUNTIME_SDK_URLS = "/getAppRuntimeSdkUrls";

    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    public static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean isMac() {
        return IS_MAC;
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

    /**
     * stm 应用数据目录，默认为：~/.stm
     */
    public static String getAppHome() {
        String appHome = Solon.cfg().get("stm.appHome");
        if (appHome == null) {
            appHome = SystemPropsUtil.get("user.home") + "/.stm";
        }
        return appHome;
    }

    /**
     * 应用软件包位置
     */
    public static String getAppPackage() {
        return getAppHome() + "/app";
    }

    /**
     * 应用运行时环境位置
     */
    public static String getAppRuntimeHome() {
        return getAppHome() + "/runtime";
    }

    /**
     * 临时目录，用于存放下载的文件
     */
    public static String getAppTmp() {
        return getAppHome() + "/tmp/" + System.currentTimeMillis();
    }

    public static String getAppPath(StmAppDO stmAppDO) {
        String appPackage = getAppPackage();
        return "%s/%s/%s".formatted(appPackage, stmAppDO.getName(), stmAppDO.getVersion());
    }

    public static String getAppRuntimePath(String appType, Long requiredVersion) {
        String appRuntimeHome = getAppRuntimeHome();
        return "%s/%s/%s".formatted(appRuntimeHome, appType.toLowerCase(), requiredVersion);
    }

    public static boolean isDebugMode() {
        String stmDebug = Solon.cfg().get("stm.debug");
        return Solon.cfg().isDebugMode() || "1".equals(stmDebug);
    }

    public static String getJavaHome(Long requiredJreVersion) {
        String appRuntimePath = getAppRuntimePath(ApplicationType.java.getType(), requiredJreVersion);
        File file = new File(appRuntimePath);
        // 目录不存在，下载对应版本的 jre
        if (!file.exists()) {
            FileUtil.mkdir(file);

            List<String> urls = apiGetAppRuntimeSdkUrls(ApplicationType.java.getType(), requiredJreVersion);
            String url = urls.get(0).split(",")[0];

//            File downloadFile = HttpUtil.downloadFileFromUrl(url, FileUtil.mkdir(getAppTmp()), new DownloadStreamProgress());
            File downloadFile = new File("/Users/songyinyin/.stm/tmp/1683625245243/OpenJDK17U-jre_aarch64_mac_hotspot_17.0.7_7.tar.gz");
            String downloadFileName = downloadFile.getName();
            if (downloadFileName.endsWith(".zip")) {
                ZipUtil.unzip(downloadFile, file);
            } else if (downloadFileName.endsWith("tar.gz")){
                GzipUtil.extractTarGZ(downloadFile, appRuntimePath);
            } else {
                throw new StmException("不支持的文件格式：%s".formatted(downloadFileName));
            }
        }
        if (isMac()) {
            String[] list = file.list();
            if (list != null) {
                List<String> subFiles = CollUtil.newArrayList(list);
                subFiles.remove(".DS_Store");
                appRuntimePath = "%s/%s/Contents/Home".formatted(appRuntimePath, subFiles.get(0));
            }
        }
        if (isDebugMode()) {
            log.info("javaHome: {}", appRuntimePath);
        }
        return appRuntimePath;
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

    public static List<String> apiGetAppRuntimeSdkUrls(String appType, Long requiredVersion) {
        if (StrUtil.isBlank(appType)) {
            throw new StmException("appType 不能为空");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appType", appType);
        paramMap.put("requiredAppTypeVersionNum", requiredVersion);
        paramMap.put("osName", getOsName());
        paramMap.put("osArch", getOsArch());
        ONode oNode = apiRequest(API_GET_APP_RUNTIME_SDK_URLS, paramMap);
        return oNode.toObjectList(String.class);
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
